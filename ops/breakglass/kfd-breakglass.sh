#!/usr/bin/env bash
#
# kfd-breakglass.sh — emergency admin recovery for KFD.
#
# Restores Super Admin access when no administrator can log in. Runs entirely
# OUTSIDE the Spring application: it talks to Postgres directly, so it still works
# when the app's auth path is broken, every admin is deactivated, or the roles
# table has been corrupted.
#
# It does NOT set or reveal a password. It issues a short-lived token into the
# EXISTING password_reset_tokens table, so the recovered admin finishes via the
# normal /reset-password page. No new API endpoint, no new attack surface.
#
# Every run is recorded in admin_recovery_events and raises an alert.
#
# Usage:
#   ./kfd-breakglass.sh --email admin@kfd.org --reason "all admins offboarded" [options]
#
# Options:
#   --email <addr>      Account to recover.                             (required)
#   --reason <text>     Why this is being run. Written to the audit trail. (required)
#   --env <local|prod>  Credential source. Default: local.
#   --allow-create      Create the account if it does not exist
#                       (for "every admin was deleted").
#   --mode <mode>       reset-link (default) | set-password
#                       set-password is the fallback for a total app outage.
#   --dry-run           Print the SQL and exit. Changes nothing.
#
set -euo pipefail

# ─── Defaults ────────────────────────────────────────────────────────────────
EMAIL=""
REASON=""
ENVIRONMENT="local"
ALLOW_CREATE=false
MODE="reset-link"
DRY_RUN=false

SUPER_ADMIN_ROLE="ROLE_SUPER_ADMIN"
TOKEN_TTL_MINUTES=30

# SSM paths (prod only)
SSM_PREFIX="/config/kfd_prod/breakglass"

die() { echo "ERROR: $*" >&2; exit 1; }
info() { echo "  $*"; }

# ─── Args ────────────────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --email)        EMAIL="${2:-}"; shift 2 ;;
    --reason)       REASON="${2:-}"; shift 2 ;;
    --env)          ENVIRONMENT="${2:-}"; shift 2 ;;
    --mode)         MODE="${2:-}"; shift 2 ;;
    --allow-create) ALLOW_CREATE=true; shift ;;
    --dry-run)      DRY_RUN=true; shift ;;
    -h|--help)      sed -n '2,30p' "$0"; exit 0 ;;
    *)              die "Unknown argument: $1" ;;
  esac
done

[[ -n "$EMAIL"  ]] || die "--email is required."
[[ -n "$REASON" ]] || die "--reason is required. The audit trail is the point of this tool."
[[ "$MODE" == "reset-link" || "$MODE" == "set-password" ]] || die "--mode must be reset-link or set-password."
[[ "$ENVIRONMENT" == "local" || "$ENVIRONMENT" == "prod" ]] || die "--env must be local or prod."

command -v psql >/dev/null || die "psql not found on PATH."

# ─── Operator identity ───────────────────────────────────────────────────────
# In prod this is the AWS IAM principal; CloudTrail independently records the
# SSM fetch below, so the trail does not depend on this script being honest.
if [[ "$ENVIRONMENT" == "prod" ]]; then
  command -v aws >/dev/null || die "aws CLI not found on PATH (required for --env prod)."
  TRIGGERED_BY="$(aws sts get-caller-identity --query Arn --output text)" \
    || die "Could not resolve AWS identity. Are you authenticated?"
else
  TRIGGERED_BY="local:$(whoami)@$(hostname)"
fi

# ─── Credentials ─────────────────────────────────────────────────────────────
if [[ "$ENVIRONMENT" == "prod" ]]; then
  info "Fetching break-glass credentials from SSM (${SSM_PREFIX})..."
  ssm_get() {
    aws ssm get-parameter --name "$1" --with-decryption --query Parameter.Value --output text \
      || die "Could not read $1. Your IAM identity may lack ssm:GetParameter/kms:Decrypt on this path."
  }
  PGHOST="$(ssm_get "${SSM_PREFIX}/db-host")"
  PGDATABASE="$(ssm_get "${SSM_PREFIX}/db-name")"
  PGUSER="$(ssm_get "${SSM_PREFIX}/db-user")"
  PGPASSWORD="$(ssm_get "${SSM_PREFIX}/db-password")"
  FRONTEND_URL="$(ssm_get "${SSM_PREFIX}/frontend-url")"
else
  PGHOST="${PGHOST:-localhost}"
  PGDATABASE="${PGDATABASE:-postgres}"
  PGUSER="${PGUSER:-postgres}"
  PGPASSWORD="${PGPASSWORD:-passwordkfd}"
  FRONTEND_URL="${FRONTEND_URL:-http://localhost:3000}"
fi
export PGHOST PGDATABASE PGUSER PGPASSWORD
PGPORT="${PGPORT:-5432}"; export PGPORT

psql_q() { psql -qtAX -v ON_ERROR_STOP=1 "$@"; }

# ─── Pre-flight checks ───────────────────────────────────────────────────────
echo
echo "KFD break-glass recovery"
echo "════════════════════════════════════════════════════════════"
info "environment : $ENVIRONMENT"
info "operator    : $TRIGGERED_BY"
info "target      : $EMAIL"
info "mode        : $MODE"
info "reason      : $REASON"
echo

USER_EXISTS="$(psql_q -c "SELECT count(*) FROM users WHERE email = '${EMAIL//\'/\'\'}';")"

if [[ "$USER_EXISTS" == "0" ]]; then
  if [[ "$ALLOW_CREATE" != "true" ]]; then
    die "No user with email '$EMAIL'. Re-run with --allow-create to provision a new Super Admin."
  fi
  ACTION="CREATE_SUPER_ADMIN"
  info "Account does not exist — will be created as Super Admin."
else
  ACTION="REENABLE"
  CURRENT="$(psql_q -c "
    SELECT COALESCE(r.name,'(none)') || ' / active=' || u.is_active
    FROM users u LEFT JOIN roles r ON r.id = u.role_id
    WHERE u.email = '${EMAIL//\'/\'\'}';")"
  info "Current state: $CURRENT"
fi

# Report the size of the hole we are filling.
ACTIVE_SA="$(psql_q -c "
  SELECT count(*) FROM users u JOIN roles r ON r.id = u.role_id
  WHERE r.name = '${SUPER_ADMIN_ROLE}' AND u.is_active = true;")"
info "Active Super Admins right now: $ACTIVE_SA"
if [[ "$ACTIVE_SA" != "0" ]]; then
  echo
  echo "  NOTE: $ACTIVE_SA Super Admin(s) can still log in. Break-glass is for a TOTAL"
  echo "        lockout — prefer the normal forgot-password flow if any admin is reachable."
  echo
fi

# ─── Build the recovery transaction ──────────────────────────────────────────
RESET_TOKEN="$(uuidgen | tr '[:upper:]' '[:lower:]')"
ESC_EMAIL="${EMAIL//\'/\'\'}"
ESC_REASON="${REASON//\'/\'\'}"
ESC_TRIGGER="${TRIGGERED_BY//\'/\'\'}"

if [[ "$MODE" == "set-password" ]]; then
  command -v python3 >/dev/null || die "--mode set-password needs python3 with the bcrypt module."
  python3 -c "import bcrypt" 2>/dev/null || die "python3 bcrypt module not installed (pip install bcrypt)."
  read -r -s -p "  New password for ${EMAIL}: " NEW_PASSWORD; echo
  [[ -n "$NEW_PASSWORD" ]] || die "Empty password."
  PW_HASH="$(NEW_PASSWORD="$NEW_PASSWORD" python3 -c \
    'import bcrypt,os;print(bcrypt.hashpw(os.environ["NEW_PASSWORD"].encode(),bcrypt.gensalt(rounds=10)).decode())')"
  unset NEW_PASSWORD
  CREDENTIAL_SQL="UPDATE users SET password = '${PW_HASH}' WHERE email = '${ESC_EMAIL}';"
  ACTION="${ACTION}+SET_PASSWORD"
else
  # '!' is not a valid bcrypt hash, so the account cannot be logged into until the
  # reset link is used. Same convention as a locked Unix password.
  CREDENTIAL_SQL="INSERT INTO password_reset_tokens (id, token, user_id, expiry_date)
  SELECT gen_random_uuid(), '${RESET_TOKEN}', u.id,
         now() + interval '${TOKEN_TTL_MINUTES} minutes'
  FROM users u WHERE u.email = '${ESC_EMAIL}';"
  ACTION="${ACTION}+RESET_LINK"
fi

if [[ "$ACTION" == CREATE_SUPER_ADMIN* ]]; then
  CREATE_SQL="INSERT INTO users (id, email, password, first_name, last_name, role_id, is_active)
  SELECT gen_random_uuid(), '${ESC_EMAIL}', '!', 'Recovered', 'Administrator', r.id, true
  FROM roles r WHERE r.name = '${SUPER_ADMIN_ROLE}';"
else
  CREATE_SQL="-- account already exists"
fi

SQL="BEGIN;

${CREATE_SQL}

-- Restore access: reactivate and ensure the Super Admin role.
UPDATE users SET is_active = true WHERE email = '${ESC_EMAIL}';
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = '${SUPER_ADMIN_ROLE}')
  WHERE email = '${ESC_EMAIL}';

-- Invalidate any outstanding reset tokens for this account before issuing a new one.
DELETE FROM password_reset_tokens
  WHERE user_id = (SELECT id FROM users WHERE email = '${ESC_EMAIL}');

${CREDENTIAL_SQL}

-- Audit trail. Records the IAM principal, not a users.id.
INSERT INTO admin_recovery_events (triggered_by, target_user, target_email, action, reason, source_ip)
  SELECT '${ESC_TRIGGER}', u.id, '${ESC_EMAIL}', '${ACTION}', '${ESC_REASON}', NULL
  FROM users u WHERE u.email = '${ESC_EMAIL}';

COMMIT;"

# ─── Dry run stops here ──────────────────────────────────────────────────────
if [[ "$DRY_RUN" == "true" ]]; then
  echo "──────────── DRY RUN — nothing was changed ────────────"
  echo "$SQL"
  echo "───────────────────────────────────────────────────────"
  exit 0
fi

# ─── Execute ─────────────────────────────────────────────────────────────────
echo "  Applying recovery transaction..."
echo "$SQL" | psql -qX -v ON_ERROR_STOP=1 >/dev/null
echo "  Done."
echo

# ─── Alert — automatic, not a follow-up task ─────────────────────────────────
ALERT_TEXT="KFD BREAK-GLASS USED
operator: ${TRIGGERED_BY}
target:   ${EMAIL}
action:   ${ACTION}
reason:   ${REASON}
env:      ${ENVIRONMENT}"

ALERTED=false
if [[ -n "${KFD_BREAKGLASS_WEBHOOK:-}" ]]; then
  if curl -fsS -X POST -H 'Content-Type: application/json' \
      --data "$(python3 -c 'import json,os;print(json.dumps({"text":os.environ["T"]}))' T="$ALERT_TEXT")" \
      "$KFD_BREAKGLASS_WEBHOOK" >/dev/null; then
    echo "  Alert sent to webhook."; ALERTED=true
  else
    echo "  WARNING: webhook alert FAILED." >&2
  fi
fi
if [[ -n "${KFD_BREAKGLASS_SNS_TOPIC:-}" ]] && command -v aws >/dev/null; then
  if aws sns publish --topic-arn "$KFD_BREAKGLASS_SNS_TOPIC" \
      --subject "KFD break-glass used" --message "$ALERT_TEXT" >/dev/null; then
    echo "  Alert published to SNS."; ALERTED=true
  else
    echo "  WARNING: SNS alert FAILED." >&2
  fi
fi
if [[ "$ALERTED" != "true" ]]; then
  echo "  WARNING: no alert channel configured — set KFD_BREAKGLASS_WEBHOOK or" >&2
  echo "           KFD_BREAKGLASS_SNS_TOPIC. Notify your security contact manually." >&2
fi

# ─── Hand back to the normal login path ──────────────────────────────────────
echo
echo "════════════════════════════════════════════════════════════"
if [[ "$MODE" == "reset-link" ]]; then
  echo "Give this link to ${EMAIL} — it expires in ${TOKEN_TTL_MINUTES} minutes:"
  echo
  echo "  ${FRONTEND_URL}/reset-password?token=${RESET_TOKEN}"
else
  echo "Password set for ${EMAIL}. They should change it after logging in."
fi
echo
echo "REQUIRED FOLLOW-UP before closing the incident:"
echo "  1. Provision a SECOND Super Admin so this cannot recur."
if [[ "$ENVIRONMENT" == "prod" ]]; then
  echo "  2. Rotate ${SSM_PREFIX}/db-password — it has now been used."
fi
echo "════════════════════════════════════════════════════════════"

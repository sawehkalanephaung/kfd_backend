# Break-glass admin recovery

Emergency path back into the KFD admin panel when **no administrator can log in**.

This is not a hidden admin account. It is a documented, audited procedure that is
deliberately expensive to invoke: it requires AWS credentials, it writes an
immutable audit record, and it raises an alert the moment it runs.

## When to use it

Use it only for a **total lockout**:

- Every `ROLE_SUPER_ADMIN` account is deleted, deactivated, or unreachable.
- A bad migration or data change has left the `users` / `roles` tables unusable.

**Do not use it** when any admin can still log in — use the normal
forgot-password flow instead. The script warns you if active Super Admins exist.

## Why it lives outside the application

The recovery path must not share fate with the thing that is broken. The script
connects to Postgres directly rather than going through Spring Security, so it
still works when the app's auth path is failing. It adds **no API endpoint** —
there is nothing new for an attacker to reach over the network.

It also never sets or reveals a password by default. It issues a short-lived token
into the existing `password_reset_tokens` table, and the recovered admin finishes
through the normal `/reset-password` page.

## Usage

```bash
# Always rehearse first — prints the SQL, changes nothing
./kfd-breakglass.sh --email admin@kfd.org --reason "drill" --dry-run

# Local development
./kfd-breakglass.sh --email admin@kfd.org --reason "lost local admin"

# Production
./kfd-breakglass.sh --env prod --email admin@kfd.org \
  --reason "INC-42: all admins offboarded without successor"

# Every admin account was deleted — provision a fresh one
./kfd-breakglass.sh --env prod --email admin@kfd.org \
  --reason "INC-42" --allow-create

# Total app outage: the reset page is unreachable, so set a password directly
./kfd-breakglass.sh --env prod --email admin@kfd.org \
  --reason "INC-43" --mode set-password
```

`--reason` is mandatory. The audit trail is the point of the tool.

## One-time production setup

### 1. Least-privilege database role

The break-glass credential must not be the application's database user. Column-scoped
grants mean it cannot read password hashes or touch any content table.

```sql
CREATE ROLE kfd_breakglass LOGIN PASSWORD '<generate a strong random value>';

GRANT CONNECT ON DATABASE postgres TO kfd_breakglass;
GRANT USAGE   ON SCHEMA public     TO kfd_breakglass;

GRANT SELECT (id, email, is_active, role_id) ON users TO kfd_breakglass;
GRANT UPDATE (is_active, role_id)            ON users TO kfd_breakglass;
GRANT SELECT ON roles TO kfd_breakglass;
GRANT INSERT, DELETE ON password_reset_tokens TO kfd_breakglass;
GRANT INSERT ON admin_recovery_events TO kfd_breakglass;
```

If you intend to use `--mode set-password`, additionally:
`GRANT UPDATE (password) ON users TO kfd_breakglass;`
If you intend to use `--allow-create`, additionally:
`GRANT INSERT ON users TO kfd_breakglass;`

### 2. Store the credentials as SecureStrings

```bash
aws ssm put-parameter --type SecureString --name /config/kfd_prod/breakglass/db-password --value '<password>'
aws ssm put-parameter --type String --name /config/kfd_prod/breakglass/db-host      --value '<rds-endpoint>'
aws ssm put-parameter --type String --name /config/kfd_prod/breakglass/db-name      --value 'postgres'
aws ssm put-parameter --type String --name /config/kfd_prod/breakglass/db-user      --value 'kfd_breakglass'
aws ssm put-parameter --type String --name /config/kfd_prod/breakglass/frontend-url --value 'https://admin.kfdofficial.org'
```

Standard-tier Parameter Store is free; KMS decrypts on a handful of uses per year
round to zero. There is no monthly cost to this design.

### 3. Restrict who can unseal it

Attach a policy allowing `ssm:GetParameter` and `kms:Decrypt` on
`/config/kfd_prod/breakglass/*` to a **named group only**, and require MFA on those
identities. CloudTrail records every retrieval automatically — that log is
independent of this script and of the application.

> The EC2 instance profile used by Elastic Beanstalk must **not** be granted access
> to this path. If the running app can read the break-glass credential, a compromise
> of the app is a compromise of the recovery path.

### 4. Configure alerting

Set one of these in the operator's environment:

```bash
export KFD_BREAKGLASS_WEBHOOK='https://hooks.slack.com/services/...'
export KFD_BREAKGLASS_SNS_TOPIC='arn:aws:sns:ap-southeast-1:...:kfd-security'
```

If neither is set the script still runs but warns loudly. A break-glass event that
nobody is notified about is just a password.

## After every use

1. **Provision a second Super Admin** before closing the incident — a single admin
   is what caused the lockout in the first place.
2. **Rotate** `/config/kfd_prod/breakglass/db-password`.
3. Review the `admin_recovery_events` row and confirm it matches the incident ticket.

## Staying confident it works

Run the full procedure against **staging** once a quarter. An untested recovery
path is not a recovery path — the failure you discover during a real lockout is
the expensive kind.

## Preventive guard in the application

Separately from this script, the API refuses to delete, deactivate, or demote the
last active Super Admin (`LastSuperAdminException` → HTTP 409, enforced in
`UserService`). That closes the most common cause of lockout without anyone needing
to break the glass at all.

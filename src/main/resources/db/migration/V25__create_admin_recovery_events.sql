-- ============================================================
-- V25: Break-glass admin recovery audit trail
-- ============================================================
-- Deliberately SEPARATE from audit_logs:
--   * triggered_by records the AWS IAM principal that ran the recovery,
--     not a users.id — the app user may be the very account being repaired.
--   * target_email is denormalised so the record survives deletion of the user row.
--   * This table must stay readable even when the app's own auth schema is suspect.
--
-- Written by ops/breakglass/kfd-breakglass.sh, never by the application.

CREATE TABLE admin_recovery_events (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    triggered_by VARCHAR(255) NOT NULL,
    target_user  UUID REFERENCES users(id) ON DELETE SET NULL,
    target_email VARCHAR(255),
    action       VARCHAR(50)  NOT NULL,
    reason       TEXT         NOT NULL,
    source_ip    VARCHAR(255),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_recovery_events_created_at
    ON admin_recovery_events (created_at DESC);

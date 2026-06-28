-- ============================================================
-- V6: Update users table for Auth implementation
-- (Safe version — uses conditional checks for partial states)
-- ============================================================

-- 1. Rename existing roles to standard Spring Security 'ROLE_' prefix
UPDATE roles SET name = 'ROLE_SUPER_ADMIN' WHERE name = 'SUPER_ADMIN';
UPDATE roles SET name = 'ROLE_ADMIN'       WHERE name = 'ADMIN';
UPDATE roles SET name = 'ROLE_MANAGER'     WHERE name = 'MANAGER';
UPDATE roles SET name = 'ROLE_EDITOR'      WHERE name = 'EDITOR';

-- 2. Add new columns to users (safe — skip if already present)
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name  VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active  BOOLEAN DEFAULT TRUE;

-- 3. Migrate existing data from display_name (only if column still exists)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'display_name'
    ) THEN
        UPDATE users SET
            first_name = split_part(display_name, ' ', 1),
            last_name  = CASE
                             WHEN strpos(display_name, ' ') > 0
                             THEN substr(display_name, strpos(display_name, ' ') + 1)
                             ELSE ''
                         END;
    END IF;
END $$;

-- 4. Migrate is_active from status (only if status column still exists)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'status'
    ) THEN
        UPDATE users SET is_active = CASE WHEN status = 'ACTIVE' THEN TRUE ELSE FALSE END;
    END IF;
END $$;

-- 5. Rename password_hash to password (only if password_hash still exists)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'password_hash'
    ) THEN
        ALTER TABLE users RENAME COLUMN password_hash TO password;
    END IF;
END $$;

-- 6. Drop old columns if they still exist
ALTER TABLE users DROP COLUMN IF EXISTS display_name;
ALTER TABLE users DROP COLUMN IF EXISTS status;

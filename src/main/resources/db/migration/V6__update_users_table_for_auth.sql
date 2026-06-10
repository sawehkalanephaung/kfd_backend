-- ============================================================
-- V6: Update users table for Auth implementation
-- ============================================================

-- 1. Rename existing roles to standard Spring Security 'ROLE_' prefix
UPDATE roles SET name = 'ROLE_SUPER_ADMIN' WHERE name = 'SUPER_ADMIN';
UPDATE roles SET name = 'ROLE_MANAGER'     WHERE name = 'ADMIN';
UPDATE roles SET name = 'ROLE_EDITOR'      WHERE name = 'EDITOR';

-- 2. Add new columns to users
ALTER TABLE users ADD COLUMN first_name VARCHAR(255);
ALTER TABLE users ADD COLUMN last_name VARCHAR(255);
ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- 3. Migrate existing data (split display_name into first_name and last_name)
UPDATE users SET 
    first_name = split_part(display_name, ' ', 1),
    last_name = CASE 
                    WHEN strpos(display_name, ' ') > 0 THEN substr(display_name, strpos(display_name, ' ') + 1)
                    ELSE '' 
                END,
    is_active = CASE WHEN status = 'ACTIVE' THEN TRUE ELSE FALSE END;

-- 4. Rename password_hash to password
ALTER TABLE users RENAME COLUMN password_hash TO password;

-- 5. Drop old columns
ALTER TABLE users DROP COLUMN display_name;
ALTER TABLE users DROP COLUMN status;

-- Note: The V2 script already inserted a Super Admin user at admin@kfd.org,
-- so we do not need to insert another one here. The existing one will use ROLE_SUPER_ADMIN.

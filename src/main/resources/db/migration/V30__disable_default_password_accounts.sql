-- ============================================================
-- V30: Neutralise seed accounts still using the default password
-- ============================================================
--
-- V2__insert_demo_data.sql seeds five accounts — including admin@kfd.org with
-- the SUPER_ADMIN role — all sharing one bcrypt hash whose plaintext is the
-- trivially guessable 'admin123'. That hash is committed to the repository, so
-- every fresh database (a new environment, a rebuilt prod, a developer clone)
-- comes up with a working SUPER_ADMIN backdoor.
--
-- V2 cannot be edited in place: it is already applied, and changing it would
-- break Flyway's checksum validation and stop every existing database from
-- starting. This forward migration achieves the same outcome and runs after V2
-- on fresh installs too, so migrations complete — through this step — before the
-- application accepts traffic; there is no window where the default password is
-- live.
--
-- The WHERE clause matches ONLY the exact committed default hash, so:
--   * a fresh DB: V2 inserts the default, this scrambles it            -> closed
--   * prod still on the default: scrambled                             -> closed
--   * any account whose password was already changed (e.g. a real admin
--     who set their own): hash no longer matches, row untouched        -> safe
--
-- Affected accounts are both password-scrambled (login with the known default
-- becomes impossible; the random value is not a valid bcrypt string, so
-- verification simply fails) and deactivated (User.isEnabled() gates on
-- is_active, so Spring Security rejects them outright). Recover one the normal
-- way: reactivate it and set a real password via the admin tools or the
-- password-reset flow.

UPDATE users
SET
    password  = 'disabled-' || gen_random_uuid(),
    is_active = FALSE
WHERE password = '$2y$05$EdN/sX91VWqlzrQCoNPXTOisi3vjgmdgeN1uEIqVrleQa2TRSpsF2';

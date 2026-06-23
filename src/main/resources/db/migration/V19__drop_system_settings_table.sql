-- ============================================================
-- V19: Drop System Settings Table
-- ============================================================

-- The system_settings table was originally created for site identity, 
-- but we have hardcoded these values in the frontend for performance reasons.
DROP TABLE IF EXISTS system_settings CASCADE;

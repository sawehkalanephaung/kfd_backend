-- ============================================================
-- V28: Add term start and end dates to team members
-- ============================================================

ALTER TABLE team_members
ADD COLUMN term_start_date DATE,
ADD COLUMN term_end_date DATE;

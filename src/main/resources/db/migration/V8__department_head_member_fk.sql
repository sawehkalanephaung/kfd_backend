-- ============================================================
-- V8: Link Department head to TeamMember entity
-- ============================================================

-- Replace plain-text head_of_department with a proper FK
ALTER TABLE departments ADD COLUMN head_member_id UUID REFERENCES team_members(id) ON DELETE SET NULL;
ALTER TABLE departments DROP COLUMN IF EXISTS head_of_department;

-- Add is_kfd_chairman flag to team_members
ALTER TABLE team_members
ADD COLUMN is_kfd_chairman BOOLEAN DEFAULT FALSE;

-- Automatically flag any existing member with "Chairman" or "Director General" in their title to maintain continuity
UPDATE team_members
SET is_kfd_chairman = TRUE
WHERE LOWER(title::text) LIKE '%chairman%' 
   OR LOWER(title::text) LIKE '%director general%';

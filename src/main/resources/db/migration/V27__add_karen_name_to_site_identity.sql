-- ============================================================
-- V27: Karen organization name
-- ============================================================
-- The public header and footer show the organization name twice: the English
-- name, and the S'gaw Karen name beneath it. Only the English one was editable —
-- the Karen name was hardcoded in the Navbar and Footer components, so renaming
-- the organization left the two out of step.
--
-- A dedicated column rather than a locale-keyed JSONB: the two names are shown
-- together, always, as a bilingual lockup. There is no language switcher on the
-- public site, so this is not a translation to select between.

ALTER TABLE site_identity
    ADD COLUMN organization_name_karen VARCHAR(255);

-- Seed the value the components currently hardcode, so the migration alone
-- changes nothing visible.
UPDATE site_identity
SET organization_name_karen = 'ကီၢ်သူလ့ၤသ့ၣ်ပှၢ်ဝဲၤကျိၤ'
WHERE organization_name_karen IS NULL;

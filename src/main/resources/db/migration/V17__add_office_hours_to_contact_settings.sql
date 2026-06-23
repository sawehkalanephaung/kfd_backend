ALTER TABLE contact_settings ADD COLUMN office_hours VARCHAR(255);

UPDATE contact_settings SET office_hours = 'Mon-Fri, 08:00 - 17:00';

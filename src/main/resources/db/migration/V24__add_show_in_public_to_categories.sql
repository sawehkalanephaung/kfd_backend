-- Add show_in_public flag to post_categories
-- Defaults to true so all existing categories remain visible on the public site
ALTER TABLE post_categories
    ADD COLUMN show_in_public BOOLEAN NOT NULL DEFAULT TRUE;

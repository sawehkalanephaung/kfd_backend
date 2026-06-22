-- ============================================================
-- V14: Site Identity, Footer Links, Newsletter Subscribers
-- ============================================================

-- 1. Clean up duplicate columns from system_settings
--    (these are already handled by contact_settings and social_media_links)
ALTER TABLE system_settings DROP COLUMN IF EXISTS contact_address;
ALTER TABLE system_settings DROP COLUMN IF EXISTS contact_phone;
ALTER TABLE system_settings DROP COLUMN IF EXISTS contact_email;
ALTER TABLE system_settings DROP COLUMN IF EXISTS social_links;

-- 2. Add site identity columns
ALTER TABLE system_settings ADD COLUMN IF NOT EXISTS tagline VARCHAR(255);
ALTER TABLE system_settings ADD COLUMN IF NOT EXISTS logo_url VARCHAR(1024);

-- 3. Seed a default row if none exists
INSERT INTO system_settings (id, organization_name, tagline, footer_copyright)
SELECT gen_random_uuid(),
       'Kawthoolei Forest Department',
       'Official Government Portal',
       '© 2025 Kawthoolei Forest Department. All rights reserved.'
WHERE NOT EXISTS (SELECT 1 FROM system_settings);

-- 4. Footer link sections (column headings in the footer sitemap)
CREATE TABLE footer_link_sections (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title         VARCHAR(255) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Individual links within each footer section
CREATE TABLE footer_links (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    section_id    UUID NOT NULL REFERENCES footer_link_sections(id) ON DELETE CASCADE,
    label         VARCHAR(255) NOT NULL,
    url           VARCHAR(1024) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. Newsletter subscribers
CREATE TABLE newsletter_subscribers (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email            VARCHAR(255) NOT NULL UNIQUE,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    subscribed_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    unsubscribed_at  TIMESTAMP WITH TIME ZONE
);

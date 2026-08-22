-- ============================================================
-- V26: Site Identity (organization / brand name)
-- ============================================================
-- Restores dynamic branding. V19 dropped system_settings because these values
-- had been hardcoded in the frontend; they are now managed from the admin panel
-- again, so the organization name can change without a code deploy.
--
-- Named site_identity rather than reusing system_settings: that name was
-- explicitly dropped, and this matches the sibling settings tables
-- (contact_settings, social_media_links, newsletter_subscribers).

CREATE TABLE site_identity (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_name VARCHAR(255) NOT NULL,
    tagline           VARCHAR(255),
    logo_url          VARCHAR(1024),
    footer_copyright  VARCHAR(255),
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Seed one row so the public site always has an identity to render.
-- Values match what the frontend currently hardcodes, so this migration alone
-- changes nothing visible.
INSERT INTO site_identity (organization_name, tagline, footer_copyright)
VALUES (
    'Kawthoolei Forestry Department',
    'Official Government Portal',
    '© 2026 Kawthoolei Forestry Department. All rights reserved.'
);

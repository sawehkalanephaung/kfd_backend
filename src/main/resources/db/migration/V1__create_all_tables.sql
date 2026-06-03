-- ============================================================
-- V1: Create all tables
-- ============================================================

-- 1. roles
CREATE TABLE roles (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    permissions JSONB
);

-- 2. users
CREATE TABLE users (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email              VARCHAR(255) NOT NULL UNIQUE,
    password_hash      VARCHAR(255) NOT NULL,
    display_name       VARCHAR(255),
    avatar_url         VARCHAR(1024),
    role_id            UUID REFERENCES roles(id),
    dashboard_language VARCHAR(10),
    status             VARCHAR(50),
    last_login         TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. audit_logs
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id),
    action_type VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id   UUID,
    ip_address  VARCHAR(255),
    user_agent  VARCHAR(512),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. team_members
CREATE TABLE team_members (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name      VARCHAR(255) NOT NULL,
    last_name       VARCHAR(255) NOT NULL,
    title           JSONB,
    department      VARCHAR(255),
    bio             JSONB,
    headshot_url    VARCHAR(1024),
    display_order   INTEGER DEFAULT 0,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      UUID REFERENCES users(id),
    last_updated_by UUID REFERENCES users(id)
);

-- 5. faqs
CREATE TABLE faqs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question        VARCHAR(1024) NOT NULL,
    answer          VARCHAR(4096) NOT NULL,
    display_order   INTEGER DEFAULT 0,
    status          VARCHAR(50) DEFAULT 'DRAFT',
    created_by      UUID REFERENCES users(id),
    last_updated_by UUID REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 6. pages
CREATE TABLE pages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(255),
    title           JSONB,
    content_blocks  JSONB,
    seo_title       JSONB,
    seo_description JSONB,
    status          VARCHAR(50),
    last_updated_by UUID REFERENCES users(id),
    published_at    TIMESTAMP WITH TIME ZONE
);

-- 7. post_categories
CREATE TABLE post_categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        JSONB,
    slug        VARCHAR(255),
    description JSONB
);

-- 8. tags
CREATE TABLE tags (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       JSONB,
    slug       VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. posts
CREATE TABLE posts (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title              JSONB,
    slug               VARCHAR(255),
    excerpt            JSONB,
    content            JSONB,
    featured_image_url VARCHAR(1024),
    author_id          UUID REFERENCES users(id),
    category_id        UUID REFERENCES post_categories(id),
    status             VARCHAR(50),
    view_count         INTEGER DEFAULT 0,
    published_at       TIMESTAMP WITH TIME ZONE
);

-- 10. post_tags (junction table)
CREATE TABLE post_tags (
    post_id UUID REFERENCES posts(id),
    tag_id  UUID REFERENCES tags(id),
    PRIMARY KEY (post_id, tag_id)
);

-- 11. media_assets
CREATE TABLE media_assets (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name      VARCHAR(255),
    file_url       VARCHAR(1024),
    file_type      VARCHAR(100),
    file_size_kb   INTEGER,
    media_category VARCHAR(255),
    uploaded_by    UUID REFERENCES users(id),
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 12. global_metrics
CREATE TABLE global_metrics (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acres_protected       DECIMAL,
    carbon_offset_tons    DECIMAL,
    species_reintroduced  INTEGER,
    active_sites          INTEGER
);

-- 13. system_settings
CREATE TABLE system_settings (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_name VARCHAR(255),
    contact_address   VARCHAR(512),
    contact_phone     VARCHAR(50),
    contact_email     VARCHAR(255),
    social_links      JSONB,
    footer_copyright  VARCHAR(255)
);

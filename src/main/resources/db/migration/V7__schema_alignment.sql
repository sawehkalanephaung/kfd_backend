-- ============================================================
-- V7: Schema Alignment — Add Departments, Pages, Inquiries
--     and cross-module foreign keys
-- ============================================================

-- 1. Create departments table
CREATE TABLE departments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug                VARCHAR(255) UNIQUE NOT NULL,
    name                VARCHAR(255) NOT NULL,
    head_of_department  VARCHAR(255),
    body_content        TEXT,
    logo_id             UUID,
    hero_image_id       UUID,
    status              VARCHAR(50) DEFAULT 'ACTIVE',
    order_index         INTEGER DEFAULT 0,
    created_by          UUID REFERENCES users(id),
    last_updated_by     UUID REFERENCES users(id),
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create department_contacts table
CREATE TABLE department_contacts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id       UUID NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    name                VARCHAR(255),
    role                VARCHAR(255),
    email               VARCHAR(255),
    phone               VARCHAR(50),
    address             TEXT,
    website_url         VARCHAR(1024),
    social_links        JSONB,
    additional_details  TEXT,
    order_index         INTEGER DEFAULT 0
);

-- 3. Create department_resource_groups table
CREATE TABLE department_resource_groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id   UUID NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    title           VARCHAR(255),
    order_index     INTEGER DEFAULT 0
);

-- 4. Create department_resource_items table
CREATE TABLE department_resource_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id        UUID NOT NULL REFERENCES department_resource_groups(id) ON DELETE CASCADE,
    media_asset_id  UUID REFERENCES media_assets(id),
    display_name    VARCHAR(255),
    order_index     INTEGER DEFAULT 0
);

-- 5. Add department_id FK to team_members
--    First, drop the old plain-text department column
ALTER TABLE team_members DROP COLUMN IF EXISTS department;
ALTER TABLE team_members ADD COLUMN department_id UUID REFERENCES departments(id);

-- 6. Add department_id FK to posts
ALTER TABLE posts ADD COLUMN department_id UUID REFERENCES departments(id);

-- 7. Add department_id FK and file_path to media_assets
ALTER TABLE media_assets ADD COLUMN IF NOT EXISTS department_id UUID REFERENCES departments(id);
ALTER TABLE media_assets ADD COLUMN IF NOT EXISTS file_path VARCHAR(1024);

-- 8. Drop the old pages table (different schema) and recreate with new schema
DROP TABLE IF EXISTS pages CASCADE;
CREATE TABLE pages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(255) UNIQUE NOT NULL,
    title           VARCHAR(512) NOT NULL,
    content         TEXT,
    hero_image_id   UUID REFERENCES media_assets(id),
    status          VARCHAR(50) DEFAULT 'DRAFT',
    created_by      UUID REFERENCES users(id),
    last_updated_by UUID REFERENCES users(id),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 9. Create inquiries table
CREATE TABLE inquiries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_name     VARCHAR(255) NOT NULL,
    sender_email    VARCHAR(255) NOT NULL,
    inquiry_type    VARCHAR(100),
    subject         VARCHAR(512),
    message         TEXT NOT NULL,
    status          VARCHAR(50) DEFAULT 'NEW',
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

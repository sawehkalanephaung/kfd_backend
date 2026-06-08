-- ============================================================
-- V3: Add audit columns and constraints to CMS tables
-- ============================================================

-- posts: add audit columns missing from V1
ALTER TABLE posts
    ADD COLUMN IF NOT EXISTS created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by      UUID REFERENCES users(id),
    ADD COLUMN IF NOT EXISTS last_updated_by UUID REFERENCES users(id);

-- posts: enforce unique slug
ALTER TABLE posts
    ADD CONSTRAINT posts_slug_unique UNIQUE (slug);

-- post_categories: add audit columns
ALTER TABLE post_categories
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- post_categories: enforce unique slug
ALTER TABLE post_categories
    ADD CONSTRAINT post_categories_slug_unique UNIQUE (slug);

-- tags: enforce unique slug
ALTER TABLE tags
    ADD CONSTRAINT tags_slug_unique UNIQUE (slug);

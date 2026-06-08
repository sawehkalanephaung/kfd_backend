-- ============================================================
-- V4: Convert JSONB columns in CMS tables to TEXT
-- ============================================================
-- The original V1 schema defined several CMS fields as JSONB.
-- The CMS feature entities map these as plain String (TEXT)
-- which is simpler and sufficient for the current use case.

-- post_categories
ALTER TABLE post_categories
    ALTER COLUMN name        TYPE TEXT USING name::TEXT,
    ALTER COLUMN slug        TYPE TEXT USING slug::TEXT,
    ALTER COLUMN description TYPE TEXT USING description::TEXT;

-- tags: name/slug were also JSONB/VARCHAR, normalise to TEXT
ALTER TABLE tags
    ALTER COLUMN name TYPE TEXT USING name::TEXT,
    ALTER COLUMN slug TYPE TEXT USING slug::TEXT;

-- posts: title, excerpt, content were JSONB in V1
ALTER TABLE posts
    ALTER COLUMN title   TYPE TEXT USING title::TEXT,
    ALTER COLUMN slug    TYPE TEXT USING slug::TEXT,
    ALTER COLUMN excerpt TYPE TEXT USING excerpt::TEXT,
    ALTER COLUMN content TYPE TEXT USING content::TEXT;

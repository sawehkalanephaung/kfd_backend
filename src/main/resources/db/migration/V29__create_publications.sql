-- Publications feature: government-style publications (reports, press releases,
-- gazettes, etc.), each backed by a downloadable document and an optional
-- thumbnail — both stored as ordinary rows in the existing media_assets table.

-- 1. publication_categories (mirrors post_categories)
CREATE TABLE publication_categories (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(150) NOT NULL,
    slug           VARCHAR(255) UNIQUE NOT NULL,
    description    TEXT,
    show_in_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. publications
CREATE TABLE publications (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Core Content
    title            VARCHAR(255) NOT NULL,
    summary          TEXT,
    category_id      UUID REFERENCES publication_categories(id),

    -- Metadata
    published_date   DATE NOT NULL,
    issued_by        VARCHAR(150),
    department_id    UUID REFERENCES departments(id),
    language         VARCHAR(50) DEFAULT 'English',
    reference_no     VARCHAR(100),

    -- Assets & Routing (both FK into the existing media_assets table)
    document_id      UUID NOT NULL REFERENCES media_assets(id),
    thumbnail_id     UUID REFERENCES media_assets(id),
    slug             VARCHAR(255) UNIQUE NOT NULL,

    -- Publishing workflow
    status           VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    download_count   INTEGER NOT NULL DEFAULT 0,

    -- Audit Trails
    created_by       UUID REFERENCES users(id),
    last_updated_by  UUID REFERENCES users(id),
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

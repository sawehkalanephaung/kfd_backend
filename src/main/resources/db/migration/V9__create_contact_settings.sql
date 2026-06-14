CREATE TABLE contact_settings (
    id UUID PRIMARY KEY,
    physical_address TEXT,
    contact_email VARCHAR(255) NOT NULL,
    inquiry_types JSONB NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert a default row so it's never empty. 
-- You can modify this via the Admin API later.
INSERT INTO contact_settings (id, physical_address, contact_email, inquiry_types) 
VALUES (
    gen_random_uuid(),
    '123 KFD Street, Yangon, Myanmar',
    'info@kfd.org',
    '["General Inquiry", "Support", "Feedback", "Partnership"]'::jsonb
);

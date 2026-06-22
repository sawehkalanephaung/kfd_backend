-- Seed script for default 5 departments

-- Define a user ID to own these records
-- We use 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80' which is 'Saw Eh Ka La Nephaung' from V2

-- 1. Insert Departments
INSERT INTO departments (id, slug, name, head_member_id, body_content, status, order_index, created_by, last_updated_by) VALUES
('55555555-0001-4000-8000-000000000001', 'survey-and-documentation-unit', 'Survey and Documentation Unit', NULL, '{"en": "The Survey and Documentation Unit''s history is closely connected to the Karen people''s relationship with forests..."}', 'ACTIVE', 1, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('55555555-0002-4000-8000-000000000002', 'awareness-and-training-unit', 'Awareness and Training Unit', NULL, '{"en": "Overview of Awareness and Training Unit..."}', 'ACTIVE', 2, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('55555555-0003-4000-8000-000000000003', 'forest-protection-and-land-documentation-unit', 'Forest Protection & Land Documentation Unit', NULL, '{"en": "Overview of Forest Protection..."}', 'ACTIVE', 3, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('55555555-0004-4000-8000-000000000004', 'tree-nursery-plantation-and-forest-restoration-unit', 'Tree Nursery Plantation & Forest Restoration Unit', NULL, '{"en": "Overview of Tree Nursery..."}', 'ACTIVE', 4, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('55555555-0005-4000-8000-000000000005', 'project-unit', 'Project Unit', NULL, '{"en": "Overview of Project Unit..."}', 'ACTIVE', 5, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80');

-- 2. Insert Timeline Events (using Survey and Documentation Unit as example)
INSERT INTO department_timeline_events (id, department_id, year, title, description, order_index) VALUES
('66666666-0001-4000-8000-000000000001', '55555555-0001-4000-8000-000000000001', 'Before 1826', 'Origins of Kaw Lah ("Green Land")', 'Karen ancestors settled in an area known as Kaw Lah. The land was known for fertile soil, abundant forests, fish, and wildlife. Communities lived primarily through agriculture.', 1),
('66666666-0002-4000-8000-000000000001', '55555555-0001-4000-8000-000000000001', '1826', 'British Colonial Administration Begins', 'British colonial government established economic policies. Teak trees were declared colonial government property. Logging operations began in Tenasserim.', 2),
('66666666-0003-4000-8000-000000000001', '55555555-0001-4000-8000-000000000001', '1852-1856', 'Expansion of Logging and Formation of Forestry Department', 'Logging expanded to Pegu and Pegu Yoma Range.', 3);

-- 3. Insert Resources (MediaAssets)
INSERT INTO media_assets (id, file_name, file_url, file_type, file_size_kb, media_category, language, department_id, uploaded_by) VALUES
('77777777-0001-4000-8000-000000000001', 'Encouragement Letter.pdf', '/dummy-url/letter.pdf', 'application/pdf', 245, 'Environment', 'English', '55555555-0001-4000-8000-000000000001', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('77777777-0002-4000-8000-000000000001', 'Kawthoolei Forestry Acts (Karen).pdf', '/dummy-url/acts-karen.pdf', 'application/pdf', 245, 'Forest', 'Karen (S''gaw)', '55555555-0001-4000-8000-000000000001', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('77777777-0003-4000-8000-000000000001', 'Kawthoolei Forestry Acts (Burmese).pdf', '/dummy-url/acts-bur.pdf', 'application/pdf', 245, 'Environment', 'Burmese', '55555555-0001-4000-8000-000000000001', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80');

-- 4. Insert Department Contacts
INSERT INTO department_contacts (id, department_id, name, role, email, phone, address, website_url, office_hours, social_links, order_index) VALUES
('88888888-0001-4000-8000-000000000001', '55555555-0001-4000-8000-000000000001', 'Department of Forestry', 'P''doh Mahn Ba Tun', 'ktl1949@gmail.com', '-', 'Headquarters office, Klo Yaw Lay, Hpa An District, Kawthoolei', 'https://www.knuhq.org/departments/forestry', 'Monday to Friday, 9:00 AM - 5:00 PM', '{"facebook": "https://facebook.com", "twitter": "https://twitter.com", "linkedin": "https://linkedin.com"}', 1);

-- 5. Insert Activities (Posts) linked to department
INSERT INTO posts (id, slug, title, content, excerpt, featured_image_url, status, category_id, author_id, department_id, published_at, created_by, last_updated_by) VALUES
('99999999-0001-4000-8000-000000000001', 'meeting-concludes-guidelines', 'Meeting Concludes on Draft Guidelines for Establishment and Management of Community-Based Watershed Conservation Areas', 'Full content here', 'Congress & Organizational Meeting', NULL, 'PUBLISHED', NULL, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', '55555555-0001-4000-8000-000000000001', NOW(), 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('99999999-0002-4000-8000-000000000001', 'training-workshop', 'Meeting Concludes on Draft Guidelines for Establishment and Management of Community-Based Watershed Conservation Areas', 'Full content here', 'Training & Workshop', NULL, 'PUBLISHED', NULL, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', '55555555-0001-4000-8000-000000000001', NOW() - INTERVAL '1 YEAR', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'),
('99999999-0003-4000-8000-000000000001', 'event-activity', 'Meeting Concludes on Draft Guidelines for Establishment and Management of Community-Based Watershed Conservation Areas', 'Full content here', 'Event & Activity', NULL, 'PUBLISHED', NULL, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', '55555555-0001-4000-8000-000000000001', NOW() - INTERVAL '2 YEAR', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80');

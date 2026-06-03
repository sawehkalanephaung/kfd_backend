-- ============================================================
-- V2: Insert demo/seed data
-- ============================================================

-- --------------------------------------------------------
-- 1. roles (must be inserted before users)
-- --------------------------------------------------------
INSERT INTO roles (id, name, description, permissions) VALUES
('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'SUPER_ADMIN', 'Full system access with all permissions', '{"manage_users": true, "manage_content": true, "manage_settings": true, "view_analytics": true}'),
('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'ADMIN', 'Administrative access for content and user management', '{"manage_users": true, "manage_content": true, "manage_settings": false, "view_analytics": true}'),
('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'EDITOR', 'Can create and edit content', '{"manage_users": false, "manage_content": true, "manage_settings": false, "view_analytics": false}');

-- --------------------------------------------------------
-- 2. users (references roles; needed for FK in later tables)
-- --------------------------------------------------------
INSERT INTO users (id, email, password_hash, display_name, avatar_url, role_id, dashboard_language, status) VALUES
('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80', 'admin@kfd.org', '$2a$12$LJ3m4ks9Xk2vRz1qWdFJkOxYz8N3pQ7sT0uV2wXyZ1aB3cD4eF5gH', 'Saw Eh Ka La Nephaung', NULL, 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'en', 'ACTIVE'),
('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091', 'naw.htoo@kfd.org', '$2a$12$aB3cD4eF5gH6iJ7kL8mN9oP0qR1sT2uV3wX4yZ5aB6cD7eF8gH9i', 'Naw Htoo Paw', NULL, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'th', 'ACTIVE'),
('f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f809102', 'kyaw.min@kfd.org', '$2a$12$xY1zA2bC3dE4fG5hI6jK7lM8nO9pQ0rS1tU2vW3xY4zA5bC6dE7fG', 'Kyaw Min Aung', NULL, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'en', 'ACTIVE'),
('a7b8c9d0-e1f2-4a3b-4c5d-6e7f80910213', 'su.myat@kfd.org', '$2a$12$gH8iJ9kL0mN1oP2qR3sT4uV5wX6yZ7aB8cD9eF0gH1iJ2kL3mN4o', 'Su Myat Mon', NULL, 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'th', 'ACTIVE'),
('b8c9d0e1-f2a3-4b4c-5d6e-7f8091021324', 'thida.win@kfd.org', '$2a$12$pQ5rS6tU7vW8xY9zA0bC1dE2fG3hI4jK5lM6nO7pQ8rS9tU0vW1xY', 'Thida Win', NULL, 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'en', 'ACTIVE');

-- --------------------------------------------------------
-- 3. team_members (references users via created_by / last_updated_by)
-- --------------------------------------------------------
INSERT INTO team_members (id, first_name, last_name, title, department, bio, headshot_url, display_order, is_active, created_by, last_updated_by) VALUES
(
    '10000000-0001-4000-8000-000000000001',
    'Saw Eh Ka La', 'Nephaung',
    '{"en": "Executive Director"}',
    'Leadership',
    '{"en": "Saw Eh Ka La Nephaung is the founder and Executive Director of KFD, bringing over 15 years of experience in community development and conservation across the Karen region."}',
    NULL, 1, TRUE,
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'
),
(
    '10000000-0002-4000-8000-000000000002',
    'Naw Htoo', 'Paw',
    '{"en": "Education Program Manager"}',
    'Education',
    '{"en": "Naw Htoo Paw leads all educational initiatives at KFD, including scholarship programs and vocational training for youth in underserved communities."}',
    NULL, 2, TRUE,
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'
),
(
    '10000000-0003-4000-8000-000000000003',
    'Kyaw Min', 'Aung',
    '{"en": "IT & Infrastructure Lead"}',
    'IT Infrastructure',
    '{"en": "Kyaw Min Aung oversees all technology systems and digital infrastructure for KFD, ensuring reliable connectivity and tools for field operations."}',
    NULL, 3, TRUE,
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80',
    'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f809102'
),
(
    '10000000-0004-4000-8000-000000000004',
    'Su Myat', 'Mon',
    '{"en": "Community Outreach Coordinator"}',
    'Community Outreach',
    '{"en": "Su Myat Mon connects KFD programs with local communities, facilitating workshops, needs assessments, and partnership development across rural areas."}',
    NULL, 4, TRUE,
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091',
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091'
);

-- --------------------------------------------------------
-- 4. faqs (references users via created_by / last_updated_by)
-- --------------------------------------------------------
INSERT INTO faqs (id, question, answer, display_order, status, created_by, last_updated_by) VALUES
(
    '20000000-0001-4000-8000-000000000001',
    'What programs does KFD offer?',
    'KFD offers a range of programs including environmental conservation, community education, vocational training, healthcare outreach, and sustainable agriculture initiatives. Each program is designed to empower local communities in the Karen region.',
    1, 'PUBLISHED',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'
),
(
    '20000000-0002-4000-8000-000000000002',
    'How can I apply for the scholarship program?',
    'To apply for our scholarship program, visit the Education section of our website and complete the online application form. Applicants must be between 16–25 years old, reside in an eligible community, and submit academic transcripts along with a personal statement. The application deadline is March 31 each year.',
    2, 'PUBLISHED',
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091',
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091'
),
(
    '20000000-0003-4000-8000-000000000003',
    'How can I donate or support KFD?',
    'You can support KFD by making a one-time or recurring donation through our website. We also welcome in-kind contributions such as educational materials, medical supplies, and technology equipment. For corporate partnerships, please contact us at partnerships@kfd.org.',
    3, 'PUBLISHED',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80',
    'f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f809102'
),
(
    '20000000-0004-4000-8000-000000000004',
    'Where does KFD operate?',
    'KFD primarily operates in the Karen State of Myanmar and along the Thailand–Myanmar border regions. Our field offices are located in Mae Sot, Chiang Mai, and several community-based locations within Karen State.',
    4, 'PUBLISHED',
    'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8091',
    'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80'
),
(
    '20000000-0005-4000-8000-000000000005',
    'Does KFD offer volunteer opportunities?',
    'Yes! KFD welcomes both local and international volunteers. Volunteer placements are available in education, healthcare, environmental conservation, and administrative support. Programs typically run for a minimum of 4 weeks. Please fill out the volunteer application form on our website for more details.',
    5, 'DRAFT',
    'a7b8c9d0-e1f2-4a3b-4c5d-6e7f80910213',
    'a7b8c9d0-e1f2-4a3b-4c5d-6e7f80910213'
);

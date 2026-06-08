-- Seed super_admin user
-- Password: Password@123# (bcrypt, cost 10)
INSERT INTO auth_schema.users (user_id, username, email, password_hash, first_name, last_name, status)
VALUES (
    gen_random_uuid(),
    'super_admin',
    'admin@compliance.com',
    '$2a$10$ptEqD4P3XsoawQsU2ouBXuffJmWNfkauH5Pu65AOmKfvlo5hm4nua',
    'System',
    'Admin',
    'ACTIVE'
) ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN role
INSERT INTO auth_schema.user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM auth_schema.users u, auth_schema.roles r
WHERE u.username = 'super_admin' AND r.role_name = 'ADMIN'
ON CONFLICT DO NOTHING;
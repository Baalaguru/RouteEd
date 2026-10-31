-- Allows the starter frontend to demonstrate course creation locally.
-- Replace this fixture with authenticated organization provisioning before production deployment.
INSERT INTO organizations (id, name, slug, timezone, status)
VALUES ('00000000-0000-0000-0000-000000000001', 'Acme Learning', 'acme-learning', 'Asia/Kolkata', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

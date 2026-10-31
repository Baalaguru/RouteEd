-- Demo accounts are intentionally deterministic for local development only.
-- Password for every account below: RouteEd@123
INSERT INTO users (id, organization_id, name, email, password_hash, role, enabled)
VALUES
  ('90000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Demo Administrator', 'admin@routeed.demo', crypt('RouteEd@123', gen_salt('bf')), 'ADMIN', TRUE),
  ('90000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Ava Learner', 'ava@routeed.demo', crypt('RouteEd@123', gen_salt('bf')), 'LEARNER', TRUE),
  ('90000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Ben Learner', 'ben@routeed.demo', crypt('RouteEd@123', gen_salt('bf')), 'LEARNER', TRUE),
  ('90000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'Chitra Learner', 'chitra@routeed.demo', crypt('RouteEd@123', gen_salt('bf')), 'LEARNER', TRUE)
ON CONFLICT (id) DO NOTHING;

UPDATE courses
SET admin_id = '90000000-0000-0000-0000-000000000001'
WHERE id IN ('10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001')
  AND admin_id IS NULL;

ALTER TABLE courses
    ADD COLUMN admin_id UUID REFERENCES users(id);

ALTER TABLE modules
    ADD COLUMN skill_tag VARCHAR(100);

CREATE INDEX idx_courses_admin ON courses(admin_id);

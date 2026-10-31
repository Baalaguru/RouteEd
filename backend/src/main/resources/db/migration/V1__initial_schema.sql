CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE organizations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(160) NOT NULL,
  slug VARCHAR(100) NOT NULL UNIQUE,
  timezone VARCHAR(64) NOT NULL DEFAULT 'Asia/Kolkata',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  organization_id UUID NOT NULL REFERENCES organizations(id),
  name VARCHAR(160) NOT NULL,
  email VARCHAR(320) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  last_login_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (organization_id, email)
);

CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash VARCHAR(255) NOT NULL UNIQUE,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ,
  replacement_token_id UUID,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE courses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  organization_id UUID NOT NULL REFERENCES organizations(id),
  title VARCHAR(200) NOT NULL,
  description TEXT,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  estimated_duration_days INTEGER NOT NULL DEFAULT 28 CHECK (estimated_duration_days > 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE modules (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  content_url TEXT,
  content_text TEXT,
  default_order INTEGER NOT NULL,
  difficulty VARCHAR(32) NOT NULL DEFAULT 'BEGINNER',
  estimated_minutes INTEGER NOT NULL DEFAULT 30 CHECK (estimated_minutes > 0),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (course_id, default_order)
);

CREATE TABLE module_prerequisites (
  module_id UUID NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
  prerequisite_module_id UUID NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
  PRIMARY KEY (module_id, prerequisite_module_id),
  CHECK (module_id <> prerequisite_module_id)
);

CREATE TABLE skill_tags (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  organization_id UUID NOT NULL REFERENCES organizations(id),
  name VARCHAR(100) NOT NULL,
  description TEXT,
  UNIQUE (organization_id, name)
);

CREATE TABLE module_skills (
  module_id UUID NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
  skill_tag_id UUID NOT NULL REFERENCES skill_tags(id) ON DELETE CASCADE,
  required_level INTEGER NOT NULL DEFAULT 1 CHECK (required_level BETWEEN 1 AND 5),
  PRIMARY KEY (module_id, skill_tag_id)
);

CREATE TABLE assessments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  course_id UUID NOT NULL UNIQUE REFERENCES courses(id) ON DELETE CASCADE,
  title VARCHAR(200) NOT NULL,
  type VARCHAR(32) NOT NULL DEFAULT 'ONBOARDING',
  published BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE questions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
  skill_tag_id UUID NOT NULL REFERENCES skill_tags(id),
  question_type VARCHAR(32) NOT NULL DEFAULT 'MCQ',
  prompt TEXT NOT NULL,
  points INTEGER NOT NULL DEFAULT 1 CHECK (points > 0),
  position INTEGER NOT NULL,
  UNIQUE (assessment_id, position)
);

CREATE TABLE answer_options (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  question_id UUID NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
  option_text TEXT NOT NULL,
  is_correct BOOLEAN NOT NULL DEFAULT FALSE,
  position INTEGER NOT NULL,
  UNIQUE (question_id, position)
);

CREATE TABLE enrollments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  learner_id UUID NOT NULL REFERENCES users(id),
  course_id UUID NOT NULL REFERENCES courses(id),
  status VARCHAR(32) NOT NULL DEFAULT 'ASSESSMENT_PENDING',
  enrolled_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  completed_at TIMESTAMPTZ,
  UNIQUE (learner_id, course_id)
);

CREATE TABLE assessment_attempts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  enrollment_id UUID NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
  total_score NUMERIC(5,2),
  started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  submitted_at TIMESTAMPTZ
);

CREATE TABLE attempt_answers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  attempt_id UUID NOT NULL REFERENCES assessment_attempts(id) ON DELETE CASCADE,
  question_id UUID NOT NULL REFERENCES questions(id),
  selected_option_id UUID REFERENCES answer_options(id),
  answer_text TEXT,
  earned_points NUMERIC(5,2) NOT NULL DEFAULT 0,
  UNIQUE (attempt_id, question_id)
);

CREATE TABLE skill_scores (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  attempt_id UUID NOT NULL REFERENCES assessment_attempts(id) ON DELETE CASCADE,
  skill_tag_id UUID NOT NULL REFERENCES skill_tags(id),
  score NUMERIC(5,2) NOT NULL CHECK (score BETWEEN 0 AND 100),
  UNIQUE (attempt_id, skill_tag_id)
);

CREATE TABLE learning_paths (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  enrollment_id UUID NOT NULL UNIQUE REFERENCES enrollments(id) ON DELETE CASCADE,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  learner_summary TEXT,
  generation_latency_ms INTEGER,
  model_name VARCHAR(100),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE learning_path_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  learning_path_id UUID NOT NULL REFERENCES learning_paths(id) ON DELETE CASCADE,
  module_id UUID NOT NULL REFERENCES modules(id),
  position INTEGER NOT NULL,
  skipped BOOLEAN NOT NULL DEFAULT FALSE,
  decision_reason TEXT,
  UNIQUE (learning_path_id, module_id),
  UNIQUE (learning_path_id, position)
);

CREATE TABLE module_progress (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  path_item_id UUID NOT NULL UNIQUE REFERENCES learning_path_items(id) ON DELETE CASCADE,
  started_at TIMESTAMPTZ,
  completed_at TIMESTAMPTZ,
  assessment_score NUMERIC(5,2) CHECK (assessment_score BETWEEN 0 AND 100)
);

CREATE TABLE email_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  enrollment_id UUID NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
  email_type VARCHAR(32) NOT NULL,
  recipient VARCHAR(320) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'QUEUED',
  provider_message_id VARCHAR(255),
  failure_reason TEXT,
  retry_count INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  sent_at TIMESTAMPTZ
);

CREATE INDEX idx_courses_organization ON courses(organization_id);
CREATE INDEX idx_modules_course ON modules(course_id, default_order);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_learner ON enrollments(learner_id);
CREATE INDEX idx_path_items_path ON learning_path_items(learning_path_id, position);
CREATE INDEX idx_email_logs_status ON email_logs(status, created_at);


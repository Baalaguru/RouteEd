CREATE TABLE learner_paths (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    module_order_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    skipped_module_ids JSONB NOT NULL DEFAULT '[]'::jsonb,
    ai_rationale_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    generated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_learner_paths_learner_course ON learner_paths(learner_id, course_id, generated_at DESC);

CREATE TABLE progress_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    module_id UUID NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
    completed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (learner_id, module_id)
);

ALTER TABLE email_logs ADD COLUMN learner_id UUID REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE email_logs ALTER COLUMN enrollment_id DROP NOT NULL;
ALTER TABLE email_logs ALTER COLUMN learner_id SET NOT NULL;
CREATE INDEX idx_email_logs_learner ON email_logs(learner_id, created_at DESC);

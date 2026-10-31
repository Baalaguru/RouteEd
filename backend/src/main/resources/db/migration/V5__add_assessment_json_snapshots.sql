ALTER TABLE questions
    ADD COLUMN options_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN correct_option VARCHAR(100),
    ADD COLUMN skill_tag VARCHAR(100),
    ALTER COLUMN skill_tag_id DROP NOT NULL;

CREATE TABLE learner_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    overall_score NUMERIC(5,2),
    skill_scores_json JSONB NOT NULL DEFAULT '{}'::jsonb,
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_learner_assessments_learner ON learner_assessments(learner_id, completed_at DESC);

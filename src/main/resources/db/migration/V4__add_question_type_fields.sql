-- Add question type support fields to question_set table
-- Supports: MULTIPLE_CHOICE (default), TRUE_FALSE, TYPE_ANSWER, POLL

ALTER TABLE question_set ADD COLUMN question_type VARCHAR(20) DEFAULT 'MULTIPLE_CHOICE';
ALTER TABLE question_set ADD COLUMN time_limit INTEGER;
ALTER TABLE question_set ADD COLUMN points INTEGER DEFAULT 1000;
ALTER TABLE question_set ADD COLUMN acceptable_answers VARCHAR(500);

-- Create index for filtering by question type
CREATE INDEX IF NOT EXISTS idx_question_type ON question_set(question_type);

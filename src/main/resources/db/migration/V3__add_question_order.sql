-- =============================================
-- Add display_order column to question_set table
-- For drag-and-drop reordering of questions
-- =============================================

ALTER TABLE question_set ADD COLUMN display_order INTEGER DEFAULT 0;

-- Initialize display_order for existing questions based on their ID
UPDATE question_set SET display_order = id WHERE display_order = 0;

-- Create index for efficient ordering
CREATE INDEX IF NOT EXISTS idx_question_display_order ON question_set(challenge_id, display_order);

-- Add media support fields to question_set table
-- Supports question images and YouTube video embeds

ALTER TABLE question_set ADD COLUMN image_url VARCHAR(500);
ALTER TABLE question_set ADD COLUMN video_url VARCHAR(500);

-- Create partial index for questions with media (PostgreSQL)
-- For H2 compatibility, we use a regular index
CREATE INDEX IF NOT EXISTS idx_question_media ON question_set(challenge_id, id) WHERE image_url IS NOT NULL OR video_url IS NOT NULL;

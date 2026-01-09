-- =============================================
-- Milestone 5: Enhanced Game Experience
-- Add streak tracking, game status, and score history
-- =============================================

-- Add streak tracking for consecutive correct answers
ALTER TABLE current_game_session ADD COLUMN IF NOT EXISTS streak_json TEXT;

-- Add previous scores for showing score deltas
ALTER TABLE current_game_session ADD COLUMN IF NOT EXISTS previous_scores_json TEXT;

-- Add game status for pause/resume functionality
ALTER TABLE current_game_session ADD COLUMN IF NOT EXISTS game_status VARCHAR(20) DEFAULT 'WAITING';

-- Add pause timestamp for timer management
ALTER TABLE current_game_session ADD COLUMN IF NOT EXISTS paused_at BIGINT;

-- Index for game status queries
CREATE INDEX IF NOT EXISTS idx_game_session_status ON current_game_session(game_status);

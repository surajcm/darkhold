-- Add moderator column to game table for concurrent game support
-- This enables querying games by moderator

ALTER TABLE game ADD COLUMN IF NOT EXISTS moderator VARCHAR(255);

-- Add index for faster queries by moderator
CREATE INDEX IF NOT EXISTS idx_game_moderator_status ON game(moderator, game_status);

-- Add comment for clarity
COMMENT ON COLUMN game.moderator IS 'Username of the moderator who created this game';

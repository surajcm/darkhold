-- Add game_mode column to game table
ALTER TABLE game ADD COLUMN IF NOT EXISTS game_mode VARCHAR(20) DEFAULT 'MULTIPLAYER';

-- Add game_mode column to current_game_session table
ALTER TABLE current_game_session ADD COLUMN IF NOT EXISTS game_mode VARCHAR(20) DEFAULT 'MULTIPLAYER';

-- Update any existing records to explicitly set MULTIPLAYER mode
UPDATE game SET game_mode = 'MULTIPLAYER' WHERE game_mode IS NULL;
UPDATE current_game_session SET game_mode = 'MULTIPLAYER' WHERE game_mode IS NULL;

-- Add comments for clarity
COMMENT ON COLUMN game.game_mode IS 'Game mode: MULTIPLAYER or PRACTICE';
COMMENT ON COLUMN current_game_session.game_mode IS 'Game mode: MULTIPLAYER or PRACTICE';

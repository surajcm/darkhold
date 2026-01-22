-- Add team mode flag to game table
ALTER TABLE game ADD COLUMN team_mode BOOLEAN DEFAULT FALSE;

-- Add team data to current_game_session
ALTER TABLE current_game_session ADD COLUMN teams_json TEXT;
ALTER TABLE current_game_session ADD COLUMN player_teams_json TEXT;

-- Add team mode to game_result
ALTER TABLE game_result ADD COLUMN team_mode BOOLEAN DEFAULT FALSE;
ALTER TABLE game_result ADD COLUMN winning_team_name VARCHAR(100);
ALTER TABLE game_result ADD COLUMN winning_team_score INTEGER;

-- Add team info to participant_result
ALTER TABLE participant_result ADD COLUMN team_name VARCHAR(100);
ALTER TABLE participant_result ADD COLUMN team_color VARCHAR(20);

-- Create team_result table for analytics
CREATE TABLE IF NOT EXISTS team_result (
    id BIGSERIAL PRIMARY KEY,
    game_result_id BIGINT NOT NULL,
    team_name VARCHAR(100) NOT NULL,
    team_color VARCHAR(20) NOT NULL,
    final_score INTEGER NOT NULL,
    final_rank INTEGER NOT NULL,
    member_count INTEGER NOT NULL,
    average_score_per_member INTEGER NOT NULL,
    FOREIGN KEY (game_result_id) REFERENCES game_result(id) ON DELETE CASCADE
);

CREATE INDEX idx_team_result_game ON team_result(game_result_id);

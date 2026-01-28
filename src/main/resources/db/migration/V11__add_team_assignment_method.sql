-- Add team assignment method to current_game_session
-- Stores BALANCED, RANDOM, or MANUAL for auto-assigning players when they join
ALTER TABLE current_game_session ADD COLUMN team_assignment_method VARCHAR(20);

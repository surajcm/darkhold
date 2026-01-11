-- Analytics tables for game results and statistics

-- Game Result table
CREATE TABLE IF NOT EXISTS game_result (
    id BIGSERIAL PRIMARY KEY,
    pin VARCHAR(10) NOT NULL,
    challenge_id VARCHAR(50) NOT NULL,
    challenge_name VARCHAR(500) NOT NULL,
    moderator VARCHAR(255) NOT NULL,
    game_mode VARCHAR(20) NOT NULL,
    total_questions INTEGER NOT NULL,
    participant_count INTEGER NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NOT NULL,
    duration_minutes INTEGER NOT NULL,
    winner_username VARCHAR(255),
    winner_score INTEGER
);

-- Participant Result table
CREATE TABLE IF NOT EXISTS participant_result (
    id BIGSERIAL PRIMARY KEY,
    game_result_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    final_score INTEGER NOT NULL,
    final_rank INTEGER NOT NULL,
    correct_answers INTEGER NOT NULL,
    incorrect_answers INTEGER NOT NULL,
    max_streak INTEGER NOT NULL,
    average_answer_time_seconds INTEGER,
    FOREIGN KEY (game_result_id) REFERENCES game_result(id) ON DELETE CASCADE
);

-- Question Result table
CREATE TABLE IF NOT EXISTS question_result (
    id BIGSERIAL PRIMARY KEY,
    game_result_id BIGINT NOT NULL,
    question_number INTEGER NOT NULL,
    question_text VARCHAR(1000) NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    correct_count INTEGER NOT NULL,
    incorrect_count INTEGER NOT NULL,
    timeout_count INTEGER NOT NULL,
    average_answer_time_seconds INTEGER,
    fastest_answer_time_seconds INTEGER,
    FOREIGN KEY (game_result_id) REFERENCES game_result(id) ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_game_result_moderator ON game_result(moderator, completed_at DESC);
CREATE INDEX IF NOT EXISTS idx_game_result_challenge ON game_result(challenge_id, completed_at DESC);
CREATE INDEX IF NOT EXISTS idx_game_result_completed ON game_result(completed_at DESC);
CREATE INDEX IF NOT EXISTS idx_game_result_pin ON game_result(pin);
CREATE INDEX IF NOT EXISTS idx_participant_result_username ON participant_result(username);
CREATE INDEX IF NOT EXISTS idx_participant_result_game ON participant_result(game_result_id);
CREATE INDEX IF NOT EXISTS idx_question_result_game ON question_result(game_result_id);

-- Comments for documentation
COMMENT ON TABLE game_result IS 'Stores completed game sessions with summary statistics';
COMMENT ON TABLE participant_result IS 'Stores individual participant performance in each game';
COMMENT ON TABLE question_result IS 'Stores question-level statistics for each game';

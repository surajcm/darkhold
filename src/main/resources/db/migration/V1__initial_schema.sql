-- =============================================
-- Darkhold Initial Schema Migration
-- PostgreSQL compatible
-- =============================================

-- Member (Users) table
CREATE TABLE IF NOT EXISTS member (
    id          BIGSERIAL PRIMARY KEY,
    firstname   VARCHAR(225),
    lastname    VARCHAR(225),
    email       VARCHAR(225) NOT NULL UNIQUE,
    password    VARCHAR(255),
    photo       VARCHAR(45),
    enabled     BOOLEAN DEFAULT FALSE NOT NULL,
    createdOn   TIMESTAMP,
    modifiedOn  TIMESTAMP,
    createdBy   VARCHAR(45),
    modifiedBy  VARCHAR(45)
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(225),
    description VARCHAR(225),
    createdOn   TIMESTAMP,
    modifiedOn  TIMESTAMP,
    createdBy   VARCHAR(45),
    modifiedBy  VARCHAR(45)
);

-- Member-Roles join table
CREATE TABLE IF NOT EXISTS member_roles (
    users_id    BIGINT REFERENCES member(id),
    roles_id    BIGINT REFERENCES roles(id),
    PRIMARY KEY (users_id, roles_id)
);

-- Challenge table
CREATE TABLE IF NOT EXISTS challenge (
    challenge_id    BIGSERIAL PRIMARY KEY,
    title           VARCHAR(225) NOT NULL UNIQUE,
    description     VARCHAR(225),
    challengeowner  BIGINT REFERENCES member(id),
    createdOn       TIMESTAMP,
    modifiedOn      TIMESTAMP,
    createdBy       VARCHAR(45),
    modifiedBy      VARCHAR(45)
);

-- Question Set table
CREATE TABLE IF NOT EXISTS question_set (
    id              BIGSERIAL PRIMARY KEY,
    question        VARCHAR(425) NOT NULL,
    answer1         VARCHAR(225),
    answer2         VARCHAR(225),
    answer3         VARCHAR(225),
    answer4         VARCHAR(225),
    correctOptions  VARCHAR(225),
    challenge_id    BIGINT REFERENCES challenge(challenge_id)
);

-- Game table
CREATE TABLE IF NOT EXISTS game (
    game_id     BIGSERIAL PRIMARY KEY,
    pin         VARCHAR(225),
    gameStatus  VARCHAR(225),
    challengeId VARCHAR(225),
    createdOn   TIMESTAMP,
    modifiedOn  TIMESTAMP
);

-- Current Game Session table (for active games)
CREATE TABLE IF NOT EXISTS current_game_session (
    id                  BIGSERIAL PRIMARY KEY,
    pin                 VARCHAR(225) NOT NULL UNIQUE,
    moderator           VARCHAR(225) NOT NULL,
    users_json          TEXT,
    questions_json      TEXT,
    current_question_no INTEGER,
    scores_json         TEXT
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_member_email ON member(email);
CREATE INDEX IF NOT EXISTS idx_challenge_owner ON challenge(challengeowner);
CREATE INDEX IF NOT EXISTS idx_question_challenge ON question_set(challenge_id);
CREATE INDEX IF NOT EXISTS idx_game_pin ON game(pin);
CREATE INDEX IF NOT EXISTS idx_game_session_pin ON current_game_session(pin);

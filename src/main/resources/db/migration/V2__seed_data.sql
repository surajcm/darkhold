-- =============================================
-- Darkhold Seed Data Migration
-- PostgreSQL compatible
-- =============================================

-- Roles
INSERT INTO roles (id, name, description, createdOn, modifiedOn, createdBy, modifiedBy) VALUES
    (1, 'ADMIN', 'Administrator of the darkhold', '2019-11-20 00:00:00', '2019-11-20 00:00:00', 'admin', 'admin'),
    (2, 'GUEST', 'Guest of the darkhold', '2019-11-20 00:00:00', '2019-11-20 00:00:00', 'admin', 'admin'),
    (3, 'GAME_MANAGER', 'Manager of the darkhold', '2019-11-20 00:00:00', '2019-11-20 00:00:00', 'admin', 'admin'),
    (4, 'PARTICIPANT', 'Participant of the darkhold', '2019-11-20 00:00:00', '2019-11-20 00:00:00', 'admin', 'admin')
ON CONFLICT (id) DO NOTHING;

-- Default Admin User (password: admin)
INSERT INTO member (id, firstname, lastname, email, password, enabled, createdOn, modifiedOn, createdBy, modifiedBy) VALUES
    (1, 'Administrator', 'Of Darkhold', 'admin@admin.com', '$2a$10$l0Y76CoFAdXlkZZYkVDsIeRP..4UcpuhQ01H0B4QvTNF2uATZx9Gq', true, '2022-12-27 00:00:00', '2022-12-27 00:00:00', 'admin', 'admin')
ON CONFLICT (email) DO NOTHING;

-- Assign Admin role to Admin user
INSERT INTO member_roles (users_id, roles_id) VALUES (1, 1)
ON CONFLICT DO NOTHING;

-- Reset sequences to proper values
SELECT setval('member_id_seq', (SELECT COALESCE(MAX(id), 1) FROM member));
SELECT setval('roles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM roles));
SELECT setval('challenge_challenge_id_seq', 1);
SELECT setval('question_set_id_seq', 1);
SELECT setval('game_game_id_seq', 1);
SELECT setval('current_game_session_id_seq', 1);

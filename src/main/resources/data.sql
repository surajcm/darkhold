insert into roles (id, name, description, createdOn, modifiedOn, createdBy, modifiedBy) values
    (1,'ADMIN','Administrator of the darkhold', '2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into roles (id, name, description, createdOn, modifiedOn, createdBy, modifiedBy) values
    (2,'GUEST','Guest of the darkhold', '2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into roles (id, name, description, createdOn, modifiedOn, createdBy, modifiedBy) values
    (3,'GAME_MANAGER','Manager of the darkhold', '2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into roles (id, name, description, createdOn, modifiedOn, createdBy, modifiedBy) values
    (4,'PARTICIPANT','Participant of the darkhold', '2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into member (id, firstname, lastname, email, password, enabled, createdOn, modifiedOn, createdBy, modifiedBy) values
    (1, 'Administrator','Of Darkhold', 'admin@admin.com', '$2a$10$l0Y76CoFAdXlkZZYkVDsIeRP..4UcpuhQ01H0B4QvTNF2uATZx9Gq',
     true, '2022-12-27 00:00:00','2022-12-27 00:00:00','admin','admin');

insert into member_roles(users_id, roles_id) values (1,1);

insert into challenge (challenge_id, title, description, challengeowner, createdOn, modifiedOn, createdBy, modifiedBy) values
    (1, 'Challenge 01', 'new Challenges', 1, '2020-04-08 00:00:00','2020-04-08 00:00:00','admin','admin');

insert into question_set (id, question, answer1, answer2, answer3, answer4, correctOptions, challenge_id) values
    (1, 'What is ECR ?',
     'A service to allow for GPU compute resources to be allocated',
     'A service to store and manage Docker container images',
     'A service to monitor performance via graphs',
     'A service to run Database As A Service',
     'B' , 1);
insert into question_set (id, question, answer1, answer2, answer3, answer4, correctOptions, challenge_id) values
    (2, 'What is LightSail ?',
     'A really nice fishing boat made of fiberglass and wood',
     'A virtual server and ssd, static IP package with a low price',
     'A serverless method to run code natively for customers',
     'A database on demand for small businesses',
     'B' , 1);
insert into question_set (id, question, answer1, answer2, answer3, answer4, correctOptions, challenge_id) values
    (3, 'What is AWS Elastic Beanstalk ?',
     'Easy to use service for deploying and scaling web apps',
     'A virtual server environment at a predictable monthly price',
     'Durable low cost storage which can be used to archive data',
     'A small village just outside of London',
     'A' , 1);
insert into question_set (id, question, answer1, answer2, answer3, answer4, correctOptions, challenge_id) values
    (4, 'What is Amazon Glacier ?',
     'Durable low cost storage which can be used to archive data',
     'Provides a hybrid storage service between on-prem and AWS',
     'Deploy scale and manage in-memory cache in the cloud',
     'Amazon relational database combining speed and simplicity',
     'A' , 1);

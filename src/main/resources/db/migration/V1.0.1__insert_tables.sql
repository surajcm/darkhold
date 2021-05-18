insert into role (id,name,createdOn, modifiedOn, createdBy, modifiedBy) values
(1,'ADMIN','2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into user (id, username, password, role, createdOn, modifiedOn, createdBy, modifiedBy) values
(1, 'admin12345', '$2a$10$5uutYZ7MJRZUKOdPR4n0xet5rzQRzpf2Ng4OhPqkOk5LXOvZS5vI6', 1,
 '2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

insert into challenge (challenge_id, title, description, createdOn, modifiedOn, createdBy, modifiedBy) values
(1, 'Challenge 01', 'new Challenges', '2020-04-08 00:00:00','2020-04-08 00:00:00','admin','admin');

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

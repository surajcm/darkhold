insert into role (id,name,createdOn, modifiedOn, createdBy, modifiedBy) values
(1,'ADMIN','2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');
insert into user (id, username, password, role, createdOn, modifiedOn, createdBy, modifiedBy) values
(1, 'admin12345', '$2a$10$5uutYZ7MJRZUKOdPR4n0xet5rzQRzpf2Ng4OhPqkOk5LXOvZS5vI6', 1,
'2019-11-20 00:00:00','2019-11-20 00:00:00','admin','admin');

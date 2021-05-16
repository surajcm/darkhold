
alter table challenge add challengeowner bigint;
alter table challenge add foreign key (challengeowner) REFERENCES user (id);

update challenge set challengeowner = 1 where challenge_id = 1 ;
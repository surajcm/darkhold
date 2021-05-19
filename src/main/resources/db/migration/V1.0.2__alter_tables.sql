
alter table challenge add challengeowner bigint;
alter table challenge add foreign key (challengeowner) REFERENCES user (id);

update challenge set challengeowner = 1 where challenge_id in (select challenge_id FROM challenge where challengeowner = null);


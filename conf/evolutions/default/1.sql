# --- !Ups

CREATE SCHEMA test;

CREATE TABLE test.users (
    id serial NOT NULL,
    name text NOT NULL
);

# --- !Downs

DROP TABLE test.users;
DROP SCHEMA test;
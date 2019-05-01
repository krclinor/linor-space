--set search_path to new;
--
--drop table if exists singer2 cascade;
--create table singer2(
--  id serial primary key,
--  first_name varchar(60) not null,
--  last_name varchar(60) not null,
--  birth_date date,
--  version int default 0,
--  constraint singer2_uq_01 unique(first_name, last_name)
--);

drop table if exists singer2 cascade;

CREATE TABLE SINGER2 (
ID INT NOT NULL AUTO_INCREMENT
, FIRST_NAME VARCHAR(60) NOT NULL
, LAST_NAME VARCHAR(40) NOT NULL
, BIRTH_DATE DATE
, VERSION INT NOT NULL DEFAULT 0
, UNIQUE UQ_SINGER_1 (FIRST_NAME, LAST_NAME)
, PRIMARY KEY (ID)
);

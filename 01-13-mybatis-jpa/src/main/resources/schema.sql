set search_path to singer;

drop table if exists singer1 cascade;
create table singer1(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  version int default 0,
  constraint singer1_uq_01 unique(first_name, last_name)
);

drop table if exists singer2 cascade;
create table singer2(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  version int default 0,
  constraint singer2_uq_01 unique(first_name, last_name)
);

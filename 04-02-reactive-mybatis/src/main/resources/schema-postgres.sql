drop table if exists singer cascade;
create table singer(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  version int default 0,
  constraint singer_uq_01 unique(first_name, last_name)
);


drop table if exists item_source cascade;
create table item_source(
  item_id varchar(10) primary key,
  description varchar(50) not null,
  polled boolean not null
);

drop table if exists item_dest cascade;
create table item_dest(
  item_id varchar(10) primary key,
  description varchar(50) not null
);
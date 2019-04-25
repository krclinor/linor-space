set search_path to singer;

drop table if exists user_role cascade;
drop table if exists users cascade;
drop table if exists roles cascade;

create table users(
	id varchar(20) primary key,
	name varchar(100) not null,
	email varchar(100),
	password varchar(255),
	enabled bool,
	last_password_reset_date timestamp
);

create table roles(
	id varchar(20) primary key,
	name varchar(255) not null
);

create table user_role(
	user_id varchar(20) references users(id) on delete cascade,
	role_id varchar(20) references roles(id) on delete cascade,
	primary key(user_id, role_id)
);




drop table if exists singer cascade;

create table singer(
  id serial not null primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  constraint singer_uq_01 unique(first_name, last_name)
);

drop table if exists album cascade;

create table album(
  id serial not null primary key,
  singer_id integer not null,
  title varchar(100) not null,
  release_date date,
  constraint album_uq_01 unique(singer_id, title),
  constraint album_fk_01 foreign key (singer_id) references singer(id) on delete cascade
);


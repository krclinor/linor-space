--set search_path to singer;

drop table if exists user_role cascade;
drop table if exists users cascade;
drop table if exists roles cascade;

create table users(
	id varchar(20) primary key,
	name varchar(100) not null,
	email varchar(100),
	password varchar(255)
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


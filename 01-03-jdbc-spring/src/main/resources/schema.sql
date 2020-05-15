drop table if exists singer cascade;
create table singer(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  constraint singer_uq_01 unique(first_name, last_name)
);

drop table if exists album cascade;
create table album(
  id serial primary key,
  singer_id int not null,
  title varchar(100) not null,
  release_date date,
  constraint album_uq_01 unique(singer_id, title),
  constraint album_fk_01 foreign key (singer_id) references singer(id) on delete cascade
);

drop table if exists instrument cascade;
create table instrument(
  instrument_id varchar(20) not null primary key
);

drop table if exists singer_instrument cascade;
create table singer_instrument(
  singer_id int not null,
  instrument_id varchar(20) not null,
  constraint singer_instrument_pk 
    primary key (singer_id, instrument_id),
  constraint fk_singer_instrument_fk_01 
    foreign key(singer_id) 
    references singer(id) 
    on delete cascade,
  constraint fk_singer_instrument_fk_02 
    foreign key(instrument_id) 
    references instrument(instrument_id)
    on delete cascade
);
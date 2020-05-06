insert into singer(first_name, last_name, birth_date, version)
values ('종서','김','1970-12-09', 0);
insert into singer(first_name, last_name, birth_date, version)
values ('건모','김','1999-07-12', 0);
insert into singer(first_name, last_name, birth_date, version)
values ('용필','조','1978-06-28', 0);

insert into album(singer_id, title, release_date, version)
values (1, '아름다운 구속','2019-01-01', 0);
insert into album(singer_id, title, release_date, version)
values (1, '날개를 활짝펴고','2019-02-01', 0);
insert into album(singer_id, title, release_date, version)
values (2, '황혼의 문턱','2019-03-01', 0);

insert into instrument (instrument_id)
values ('기타'); 
insert into instrument (instrument_id)
values ('피아노'); 
insert into instrument (instrument_id)
values ('드럼'); 
insert into instrument (instrument_id)
values ('신디사이저');

insert into singer_instrument(singer_id, instrument_id)
values (1, '기타');
insert into singer_instrument(singer_id, instrument_id)
values (1, '피아노');
insert into singer_instrument(singer_id, instrument_id)
values (2, '기타');
insert into singer_instrument(singer_id, instrument_id)
values (3, '드럼');



--insert into singer (first_name, last_name, birth_date)
--values ('John', 'Mayer', '1977-10-16');
--insert into singer (first_name, last_name, birth_date)
--values ('Eric', 'Clapton', '1945-03-30');
--insert into singer (first_name, last_name, birth_date)
--values ('John', 'Butler', '1975-04-01');
--insert into album (singer_id, title, release_date)
--values (1, 'The Search For Everything', '2017-01-20');
--insert into album (singer_id, title, release_date)
--values (1, 'Battle Studies', '2009-11-17');
--insert into album (singer_id, title, release_date)
--values (2, 'From The Cradle ', '1994-09-13');
--insert into instrument (instrument_id) values ('Guitar');
--insert into instrument (instrument_id) values ('Piano');
--insert into instrument (instrument_id) values ('Voice');
--insert into instrument (instrument_id) values ('Drums');
--insert into instrument (instrument_id) values ('Synthesizer');
--insert into singer_instrument(singer_id, instrument_id) values (1, 'Guitar');
--insert into singer_instrument(singer_id, instrument_id) values (1, 'Piano');
--insert into singer_instrument(singer_id, instrument_id) values (2, 'Guitar');
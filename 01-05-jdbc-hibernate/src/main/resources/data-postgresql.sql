insert into singer(first_name, last_name, birth_date)
values 
('종서','김','1970-12-09'),
('건모','김','1999-07-12'),
('용필','조','1978-06-28');

insert into album(singer_id, title, release_date)
values 
(1, '아름다운 구속','2019-01-01'),
(1, '날개를 활짝펴고','2019-02-01'),
(2, '황혼의 문턱','2019-03-01');

insert into instrument (instrument_id)
values 
('기타'), ('피아노'), ('드럼'), ('신디사이저');

insert into singer_instrument(singer_id, instrument_id)
values 
(1, '기타'),
(1, '피아노'),
(2, '기타'),
(3, '드럼');
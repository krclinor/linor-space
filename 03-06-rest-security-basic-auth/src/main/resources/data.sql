insert into users(id, name, email, password) values
('admin', '관리자', 'admin@gmail.com','$2a$10$i5PIsD4ak2IYV3aVhps4XuIF2bvKi54JjmItJ/qGz2ogVJjE/ycDy'),
('linor', '리노', 'linor@gmail.com', '$2a$10$Z6wZsEYowSJRTPoRNsIiRO68817rTLeOLlnwcnQ2LCHZKdsDgU65y'),
('user', '데모 사용자', 'user@gmail.com', '$2a$10$GmyV4/PqvfgcSHgTX6WYBeeY9STc.rvSTZAmYrMbqhW1dJZm6eLAe');

insert into roles(id, name) values
('ADMIN', '관리자 권한'),
('USER', '사용자 권한');

insert into user_role(user_id, role_id) values
('admin', 'ADMIN'),
('admin', 'USER'),
('linor', 'ADMIN'),
('linor', 'USER'),
('user', 'USER');


insert into singer(first_name, last_name, birth_date)
values 
('종서','김','1970-12-09'),
('건모','김','1999-07-12'),
('용필','조','1978-06-28'),
('진아','태','2000-11-01');

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
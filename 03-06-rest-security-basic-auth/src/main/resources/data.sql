insert into users(id, name, email, password) values
('admin', '관리자', 'admin@gmail.com','$2a$10$i5PIsD4ak2IYV3aVhps4XuIF2bvKi54JjmItJ/qGz2ogVJjE/ycDy'),
('linor', '리노', 'linor@gmail.com', '$2a$10$Z6wZsEYowSJRTPoRNsIiRO68817rTLeOLlnwcnQ2LCHZKdsDgU65y'),
('user', '데모 사용자', 'user@gmail.com', '$2a$10$GmyV4/PqvfgcSHgTX6WYBeeY9STc.rvSTZAmYrMbqhW1dJZm6eLAe');

insert into roles(id, name) values
('ROLE_ADMIN', '관리자 권한'),
('ROLE_USER', '사용자 권한');

insert into user_role(user_id, role_id) values
('admin', 'ROLE_ADMIN'),
('admin', 'ROLE_USER'),
('linor', 'ROLE_ADMIN'),
('linor', 'ROLE_USER'),
('user', 'ROLE_USER');


insert into singer(first_name, last_name, birth_date)
values
('종서','김','19701209'),
('건모','김','19990712'),
('용필','조','19780628'),
('진아','태','20001101');

insert into album(singer_id, title, release_date)
values
(1, '아름다운 구속','20190101'),
(1, '날개를 활짝펴고','20190201'),
(2, '황혼의 문턱','20190301');



insert into singer(first_name, last_name, birth_date)
values 
('종서','김','1970-12-09'),
('건모','김','1999-07-12'),
('용필','조','1978-06-28');

with s as (
select * from generate_series(1,10) i
)
insert into singer.singer(first_name, last_name, birth_date)
select 'firstName'||s.i, 'lastName'||s.i, '2019-08-19'
from s;
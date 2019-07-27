# JDBC Todo
사용 데이타베이스는 postgresql을 사용한다.
postgresql을 설치 후 spring계정을 생성한다.

## 1. 계정 생성
```sql
CREATE USER spring WITH
	LOGIN
	NOSUPERUSER
	NOCREATEDB
	NOCREATEROLE
	INHERIT
	NOREPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'sring1234';
로그인이 가능한 spring 계정으로 비밀번호는 'spring1234'로 설정하였다.

## 2. 
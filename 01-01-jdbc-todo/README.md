# JDBC Todo
사용 데이타베이스는 postgresql을 사용한다.
postgresql을 설치 후 Spring Boot에서 사용할 수 있도록 설정한다.


## 1. 계정 생성
```sql
CREATE USER linor WITH
	LOGIN
	NOSUPERUSER
	NOCREATEDB
	NOCREATEROLE
	INHERIT
	NOREPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'sring1234';
```
로그인이 가능한 linor 계정으로 비밀번호는 'linor1234'로 설정하였다.

## 2. 데이타베이스 생성
```sql
CREATE DATABASE spring
    WITH 
    OWNER = linor
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
```
spring이라는 데이타베이스를 생성하고 소유자는 위에서 만든 linor로 한다.

## 3. 스키마 생성
```sql
CREATE SCHEMA singer
    AUTHORIZATION linor;
```
마지막으로 singer라는 스키마를 생성하고 소유자는 linor로 한다.


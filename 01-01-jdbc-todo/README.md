# JDBC Todo
Spring Boot JDBC개발을 진행하기 위한 준비과정으로 데이타베이스 설정,  
lombok설정, dao인터페이스, 테스트케이스를 준비한다.

## 이클립스 추가 플러그인 설치
Spring Tool Suite
Lombok

## 데이타베이스 설정
### 계정 생성
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

### 데이타베이스 생성
```sql
CREATE DATABASE spring
    WITH 
    OWNER = linor
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
```
spring이라는 데이타베이스를 생성하고 소유자는 위에서 만든 linor로 한다.

### 스키마 생성
```sql
CREATE SCHEMA singer
    AUTHORIZATION linor;
```
마지막으로 singer라는 스키마를 생성하고 소유자는 linor로 한다.

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.
추가할 dependency : devtools, lombok, postgresql

### 스프링 부트 application.yml설정
src/main/resources/application.yml에 스프링부트에서 사용할 데이타소스를 설정한다.
```yml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: always
```


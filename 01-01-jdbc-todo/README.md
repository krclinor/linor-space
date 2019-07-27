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

### application.yml설정
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
yml은 properties파일보다 시스템 설정사항을 효율적으로 관리할 수 있다.
datasource 설정에 필요한 속성은 driver-class-name, url, username, password이다.
initialization-mode를 always로 하면 spring boot시작시 schema.sql과 data.sql파일을 
실행하여 데이타베이스의 테이블을 생성하고 데이타를 로드하여 데이타베이스를 초기화 한다.

### 데이타베이스 초기화 파일 생성
#### schema.sql
```sql
set search_path to singer;

drop table if exists singer cascade;

create table singer(
  id serial not null primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  constraint singer_uq_01 unique(first_name, last_name)
);

drop table if exists album cascade;

create table album(
  id serial not null primary key,
  singer_id integer not null,
  title varchar(100) not null,
  release_date date,
  constraint album_uq_01 unique(singer_id, title),
  constraint album_fk_01 foreign key (singer_id) references singer(id) on delete cascade
);

```
#### data.sql
```sql
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
```

### DAO인터페이스 생성
데이타베이스를 이용한 처리 인터페이스 선언으로 향후 이 인터페이스를 구현할 예정이다.
파일명 :src/main/java/com.linor.singer.dao.SingerDao.java
```java
package com.linor.singer.dao;

import java.util.List;

import com.linor.singer.domain.Singer;

public interface SingerDao {
    List<Singer> findAll();
    List<Singer> findByFirstName(String firstName);
    String findNameById(Integer id);
    Singer findById(Integer id);
    String findFirstNameById(Integer id);
    void insert(Singer singer);
    void update(Singer singer);
    void delete(Integer singerId);
    List<Singer> findAllWithAlbums();
    void insertWithAlbum(Singer singer);
}
```

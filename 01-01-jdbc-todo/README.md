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
### Domain 클래스 생성
도메인 클래스는 보통 테이블에 대응되는 엔터티 클래스로 Singer와 Album클래스를 생성한다.  
파일명: com.linor.singer.domain.Singer.java
```java
package com.linor.singer.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Singer {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Album> albums;

    public void addAlbum(Album album) {
        if(albums == null) {
            albums = new ArrayList<>();
        }
        albums.add(album);
    }
}
```
파일명: com.linor.singer.domain.Album.java
```java
package com.linor.singer.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Album {
    private Integer id;
    private Integer singerId;
    private String title;
    private LocalDate releaseDate;
}
```

### DAO인터페이스 생성
데이타베이스를 이용한 처리 인터페이스 선언으로 향후 이 인터페이스를 구현할 예정이다.  
파일명 :com.linor.singer.dao.SingerDao.java
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

## Test Case 생성
파일명: com.linor.singer.SingerDaoTests.java
```java
package com.linor.singer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.linor.singer.dao.SingerDao;
import com.linor.singer.domain.Singer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@RequiredArgsConstructor
@Slf4j
public class SingerDaoTests {
    private final SingerDao singerDao;
    
    @Test
    public void contextLoads() {
    }
    
    @Test
    public void testFindNameById() {
        String name = singerDao.findNameById(1);
        assertTrue("종서 김".equals(name));
    }

    @Test
    public void testFindAll(){
        List<Singer> singers = singerDao.findAll();
        assertNotNull(singers);
        assertTrue(singers.size() == 4);
        log.info("가수목록");
        listSingers(singers);
        
        Singer singer = new Singer();
        singer.setFirstName("길동");
        singer.setLastName("홍");
        singer.setBirthDate(LocalDate.parse("1977-10-16"));
        singerDao.insert(singer);
        
        singers = singerDao.findAll();
        assertTrue(singers.size() == 5);
        log.info("가수 추가 후 가수 목록");
        listSingers(singers);
        
        singerDao.delete(singer.getId());
        singers = singerDao.findAll();
        assertTrue(singers.size() == 4);
        log.info("가수 삭제 후 가수 목록");
        listSingers(singers);
        
    }
    
    
    private void listSingers(List<Singer> singers){
        for(Singer singer: singers){
            log.info(singer.toString());
        }
    }

    @Test
    public void testFindAllWidthAlbums() {
        List<Singer> singers = singerDao.findAllWithAlbums();
        assertTrue(singers.size() == 4);
        singers.forEach(singer -> {
            log.info(singer.toString());
        });
    }
    
    @Test
    public void testFindByFirstName() {
        List<Singer> singers = singerDao.findByFirstName("종서");
        assertTrue(singers.size() == 1);
        listSingers(singers);
    }

    @Test
    public void testFindbyId() {
        Singer singer = singerDao.findById(1);
        log.info("주키로 1개 레코드 검색 결과>>>");
        log.info(singer.toString());
    }
    
    @Test
    public void testSingerUpdate() {
        Singer singerOldSinger = singerDao.findById(1);
        log.info(">>> 김종서 수정 전 >>>");
        log.info(singerOldSinger.toString());
        Singer singer = new Singer();
        singer.setId(1);
        singer.setFirstName("종서");
        singer.setLastName("김");
        singer.setBirthDate(LocalDate.parse("1977-10-16"));
        singerDao.update(singer);
        Singer singerNewSinger = singerDao.findById(1);
        log.info(">>> 김종서 수정 후 >>>");
        log.info(singerNewSinger.toString());
    }
}
```
todo 프로젝트는 SpringBoot로 실행은 가능하나 테스트케이스는 실행되지 않는다.  
이 프로젝트는 다음에 진행할 JDBC프로젝트의 기본 구조로 사용된다.

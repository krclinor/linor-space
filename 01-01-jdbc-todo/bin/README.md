# JDBC Todo
이 프로젝트는 SpringBoot로 실행은 가능하나 테스트케이스는 실행되지 않는다.  
이 프로젝트는 다음에 진행할 모든 JDBC프로젝트의 기본 구조로 사용한다.  
사용하는 DBMS는 postgresql이다.  

Spring Boot JDBC개발을 진행하기 위한 준비과정으로 데이타베이스 설정,    
lombok설정, dao인터페이스, 테스트케이스를 준비한다.  

## 이클립스 추가 플러그인 설치
예제 프로젝트를 정상적으로 연습할 수 있도록 Spring Tool Suite와 Lombok가 설치되어 있어야 한다.  

## 데이타베이스 설정
postgres계정으로 다음 sql문들을 실행한다.  
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

### 의존성 라이브러리
의존성 라이브러리 : devtools, lombok, postgresql, jdbc  

소스 : [pom.xml](pom.xml)
```xml
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
    </dependencies>
```
## 설정
### 데이타 소스 설정
스프링부트에서 사용할 데이타소스를 설정한다.  
소스 : [application.yml](src/main/resources/application.yml)
```yml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: always
```
yml파일이 properties파일보다 시스템 설정사항을 효율적으로 관리할 수 있다.
datasource 설정에 필요한 속성은 driver-class-name, url, username, password이다.
initialization-mode를 always로 하면 spring boot시작시 schema.sql과 data.sql파일을 
실행하여 데이타베이스의 테이블을 생성하고 데이타를 로드하여 데이타베이스를 초기화 한다.

### 데이타베이스 초기화 파일 생성
프로젝트 실행시 사용할 테이블을 생성하기 한 sql script파일로 application.yml에서     
spring.datasource.initialization-mode가 always인 경우 실행된다.  

소스 : [schema.sql](src/main/resources/schema.sql)
```sql
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

소스 : [data.sql](src/main/resources/data.sql)
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

## Domain 클래스 생성
도메인 클래스는 보통 테이블에 대응되는 엔터티 클래스로 Singer와 Album클래스를 생성한다.  
  
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)
```java
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
Data어노테이션은 Lombok에서 제공하는 것으로 자동으로 get/set메서드를 생성하여 코딩을 깔끔하게 작성할 수 있다.


소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)
```java
@Data
public class Album {
    private Integer id;
    private Integer singerId;
    private String title;
    private LocalDate releaseDate;
}
```

## DAO인터페이스 생성
데이타베이스를 이용한 처리 인터페이스 선언으로 향후 이 인터페이스를 구현할 예정이다.  

소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  
```java
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
Dao인터페이스를 테스트 할 테스트케이스를 생성한다.  
소스 : [SinerDaoTests.java](src/test/java/com/linor/singer/SingerDaoTests.java)
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@RequiredArgsConstructor
@Slf4j
public class SingerDaoTests {
    @Autowired
    private SingerDao singerDao;
    
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
테스트 케이스는 Junit 테스트 케이스로 현재 프로젝트에서는 인터페이스 구현체가 없어서 실행되지 않지만 
나머지 프로젝트에서 구현체를 생성 후 Junit을 실행하면 모두 실행되도록 하였다.
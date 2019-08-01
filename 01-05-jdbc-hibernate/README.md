# Hibernate
Hibernate로 다대다의 관계를 표현하기 위해 악기테이블을 추가하여 스키마를 구성하여 작업한다.

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.
추가할 dependency : devtools, lombok, postgresql, jpa, hibernate-types-52
pom.xml파일에 다음 내용을 확인할 수 있다.
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
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <!-- Hibernate CamelCase를 SnakeCase로 변경 -->
        <dependency>
            <groupId>com.vladmihalcea</groupId>
            <artifactId>hibernate-types-52</artifactId>
            <version>2.5.0</version>
        </dependency>
    </dependencies>
```
### application.yml설정
src/main/resources/application.yml에 hibernate관련 설정을 추가한다.
```yml
  jpa:
    show-sql: true
    #hibernate:
      #ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        physical-naming-strategy: com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy
        format_sql: true
        use_sql_comments: true
        jdbc.lob.non_contextual_creation: true
        #temp.use_jdbc_metadata_default: false
        current_session_context_class: org.springframework.orm.hibernate5.SpringSessionContext
```
dialect에 사용하는 데이타베이스가 postgresql이므로 org.hibernate.dialect.PostgreSQLDialect를 설정한다.  
physical-naming-strategy에 Camel Case로 작성된 객체의 프로퍼티를 Snake Case로 작성된 테이블 칼럼과 매칭될 수 있도록 
CamelCaseToSnakeCaseNamingStrategy를 설정한다.  
postgresql을 사용하는 경우 발생하는 오류를 제거하기 위해 jdbc.lob.non_contextual_creation을 true로 설정한다.  
getCurrentSession을 사용하기 위해서 current_session_context_class에 org.springframework.orm.hibernate5.SpringSessionContext를 
설정한다.
 

### 데이타베이스 초기화 파일 생성
#### schema.sql
```sql
set search_path to singer;

drop table if exists singer cascade;
create table singer(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  version int default 0,
  constraint singer_uq_01 unique(first_name, last_name)
);

drop table if exists album cascade;
create table album(
  id serial primary key,
  singer_id int not null,
  title varchar(100) not null,
  release_date date,
  version int default 0,
  constraint album_uq_01 unique(singer_id, title),
  constraint album_fk_01 foreign key (singer_id) references singer(id) on delete cascade
);

drop table if exists instrument cascade;
create table instrument(
  instrument_id varchar(20) not null primary key
);

drop table if exists singer_instrument cascade;
create table singer_instrument(
  singer_id int not null,
  instrument_id varchar(20) not null,
  constraint singer_instrument_pk 
    primary key (singer_id, instrument_id),
  constraint fk_singer_instrument_fk_01 
    foreign key(singer_id) 
    references singer(id) 
    on delete cascade,
  constraint fk_singer_instrument_fk_02 
    foreign key(instrument_id) 
    references instrument(instrument_id)
    on delete cascade
);
```
#### data.sql
```sql
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
```
### 엔터티 클래스 생성
가수 엔터티 클래스
```java
@Entity
//@Table(name="singer")
@NamedQueries({
    @NamedQuery(name="Singer.findById",
            query="select distinct s from Singer s " +
            "left join fetch s.albums a " +
            "left join fetch s.instruments i " +
            "where s.id = :id"),
    @NamedQuery(name="Singer.findAllWithAlbum",
            query="select distinct s from Singer s \n"
                    + "left join fetch s.albums a \n"
                    + "left join fetch s.instruments i"),
    @NamedQuery(name="Singer.findByFirstName",
    query="select distinct s from Singer s \n"
            + "where s.firstName = :firstName")
})
@Data
public class Singer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    //@Column(name="first_name")
    private String firstName;
    
    //@Column(name="last_name")
    private String lastName;
    
    //@Column(name="birth_date")
    private LocalDate birthDate;
    
    @OneToMany(mappedBy="singer", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Album> albums = new HashSet<>();

    @ManyToMany
    @JoinTable(name="singer_instrument", 
        joinColumns=@JoinColumn(name="singer_id"),
        inverseJoinColumns=@JoinColumn(name="instrument_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Instrument> instruments = new HashSet<>();
    
    @Version
    private int version;
    
    public boolean addAlbum(Album album) {
        album.setSinger(this);
        return getAlbums().add(album);
    }
    public void reoveAlbum(Album album) {
        getAlbums().remove(album);
    }
}
```
@Entity는 해당 클레스가 엔터티 클래스임을 표시한다.  
@Table은 매핑될 데이터베이스 테블을 설정한다. @Table(name = "singer")은 데이터베이스의 SINGER테이블과 매핑한다.   
클래스명과 테이블 명이 동일할 경우 생략 가능하다.  
@Id는 주키를 표시한다. @GeneratedValue는 자동생성되는 값을 설정하기 위해 사용한다.   
@GeneratedValue는 주키의 값을 위한 자동 생성 전략을 명시하는데 사용한다.  
선택적 속성으로 generator와 strategy가 있다.  
strategy는 persistence provider가 엔티티의 주키를 생성할 때 사용해야 하는 주키생성 전략을 의미한다.  
디폴트 값은 AUTO이다.  
generator는 SequenceGenerator나 TableGenerator 애노테이션에서 명시된 주키 생성자를 재사용할 때 쓰인다. 디폴트 값은 공백문자("")이다. 
주키 생성 전략으로 JPA가 지원하는 것은 아래의 네 가지이다.  
- AUTO : (persistence provider가) 특정 DB에 맞게 자동 선택  
- IDENTITY : DB의 identity 컬럼을 이용  
- SEQUENCE : DB의 시퀀스 컬럼을 이용  
- TABLE : 유일성이 보장된 데이터베이스 테이블을 이용

@Column은 매핑할 테이블을 칼럼을 표시한다.  
@Column(name="first_name")는 데이터베이스 테이블의 칼럼이 first_name이다.  
application.yml설정에서 physical-naming-strategy를 설정하였기 때문에 @Column어노테이션을 사용하지 않더라도 
firstName프로퍼티를 first_name칼럼과 매핑된다.  

@OneToMany는 1대다를 표현하기 위해 사용한다.  
mappedBy="singer"는 Album클래스에서 Singer를 나타내는 프로퍼티이다.   
@OneToMany 프로퍼티
- targetEntity : 연결을 맺는 상대 엔티티
- cascade : 관계 엔티티의 읽기 전략을 설정.
- mappedBy : 양뱡향 관계에서 주체가 되는 쪽(Many쪽, 외래키가 있는 쪽)을 정의
- orphanRemoval : 연관 관례에 있는 엔티티에서 변경이 일어난 경우 DB 변경을 같이 할지 결정.
  Cascade는 JPA 레이어의 정의이고 이 속성은 DB레이어에서 직접 처리한다. 기본은 false

@Version은 엔티티가 수정될때 자동으로 버전이 하나씩 증가하게 된다. 엔티티를 수정할 때 조회 시점의 버전과 수정 시점의 버전이 다르면 예외가 발생한다.  
예를 들어 트랜잭션 1이 조회한 엔티티를 수정하고 있는데 트랜잭션 2에서 같은 엔티티를 수정하고 커밋해서 버전이 증가해버리면 트랜잭션 1이 커밋할 때 버전 정보가 다르므로 예외가 발생한다.

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
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SingerDao singerDao;
    
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

# JPA Audit
JPA Audit(감사)기능에 대해 알아본다.
Audit은 테이블 레코드의 생성자, 생성일시, 수정자, 수정일시등의 내역을 테이블에 기록하도록 한다.  

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
        #current_session_context_class: org.springframework.orm.hibernate5.SpringSessionContext
```
dialect에 사용하는 데이타베이스가 postgresql이므로 org.hibernate.dialect.PostgreSQLDialect를 설정한다.  
physical-naming-strategy에 Camel Case로 작성된 객체의 프로퍼티를 Snake Case로 작성된 테이블 칼럼과 매칭될 수 있도록 
CamelCaseToSnakeCaseNamingStrategy를 설정한다.  
postgresql을 사용하는 경우 발생하는 오류를 제거하기 위해 jdbc.lob.non_contextual_creation을 true로 설정한다.  
show_sql을 true로 설정하면  hibernate가 생성한 sql문을 볼 수 있고,    
format_sql을 true로 설정하면 sql문을 읽기 쉽도록 만들어 준다.  
use_sql_comments를 true로 설정하면 sql문에 HQL쿼리를 주석으로 같이 보여준다.  
 

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
  created_by varchar(20),
  created_date timestamp,
  last_modified_by varchar(20),
  last_modified_date timestamp,
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
### Audit용 추상클래스 생성
엔터티 클래스에 프로퍼티를 일일이 추가해도 되지만 동일한 칼럼이 여러 테이블에 존재할 수 있기 때문에 추상클래스로 작성하는 것이 편리하다.  
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable<U> {

    @CreatedBy
    protected U createdBy;
    
    @CreatedDate
    protected LocalDateTime createdDate;
    
    @LastModifiedBy
    protected U lastModifiedBy;
    
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;
}
``` 
@MappedSuperclass로 엔터티 추상클래스임을 알린다.  
@EntityListeners(AuditingEntityListener.class)는 
Persistence Context에서 해당 엔티티를 AuditingEntityListener.class가 감사할 수 있도록 등록한다.  
@CreatedBy, @CreatedDate, @LastModifiedBy, @LastModifiedDate은 감사관련 어노테이션이다.  

### 엔터티 클래스 생성
#### Singer 엔터티 클래스(일대다, 다대다 관계)
가수 엔터티 클래스에 감사 기능을 적용한다.    
```java
@Entity
//@Table(name="singer")
@Data
public class Singer extends Auditable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    //@Column(name="first_name")
    private String firstName;
    
    //@Column(name="last_name")
    private String lastName;
    
    //@Column(name="birth_date")
    private LocalDate birthDate;
    
    @OneToMany(mappedBy="singer", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    private Set<Album> albums = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="singer_instrument", 
        joinColumns=@JoinColumn(name="singer_id"),
        inverseJoinColumns=@JoinColumn(name="instrument_id"))
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
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
Auditable클래스를 상속받은 가수 엔터티 클래스를 생성한다.  

### SingerRepository 인터페이스 생성
```java
public interface SingerRepository extends CrudRepository<Singer, Integer> {
    List<Singer> findAll();
    List<Singer> findByFirstName(String firstName);
    List<Singer> findByFirstNameAndLastName(String firstName, String lastName);
}
```
Repository 인터페이스나 Dao구현 클래스 등은 기존 JpaRepository에서 처리한 내역과 동일한다.

### AuditorAwareBean 생성 및 등록
AuditorAwareBean은 JPA감사 기능을 가능하게 한다. auditorAwareBean은 사용자 정보를 제공하는 빈이다.  
```java
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Linor");
    }

    // ------------------ Use below code for spring security --------------------------

    /*class SpringSecurityAuditorAware implements AuditorAware<User> {
     public User getCurrentAuditor() {

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null || !authentication.isAuthenticated()) {
       return null;
      }

      return ((MyUserDetails) authentication.getPrincipal()).getUser();
     }
    }*/
}

```

생성한 AuditorAwareBean을 빈에 등록한다.  
```java
@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorAware")
public class JpaAuditConfig {
    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }
}
```

### 테스트 케이스 
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SingerDaoTests {
    @Autowired
    private SingerDao singerDao;
    
    @Test
    public void testAuditSinger() {
        List<Singer> singers = singerDao.findAll();
        listSingers(singers);
        log.info("새 가수 추가");
        Singer singer = new Singer();
        singer.setFirstName("BB");
        singer.setLastName("King");
        singer.setBirthDate(LocalDate.parse("1970-12-09"));
        singerDao.save(singer);
        
        singers = singerDao.findAll();
        listSingers(singers);
        
        singer = singerDao.findById(2);
        log.info("가수  내역: " + singer.toString());
        
        singer.setFirstName("John Clayton");
        singerDao.save(singer);
        
        singer = singerDao.findById(2);
        singer.setFirstName("Riley B.");
        singerDao.save(singer);

        singers = singerDao.findAll();
        listSingers(singers);
    }
    
    private void listSingers(List<Singer> singers) {
        singers.forEach(singer -> {
            log.info(singer.toString());
            log.info("Audit: {}, {}, {}, {}" 
                    , singer.getCreatedBy()
                    , singer.getCreatedDate()
                    , singer.getLastModifiedBy()
                    , singer.getLastModifiedDate());
        });
    }
}
```
테스트 케이스를 실행하면 결과를 확인할 수 있다.

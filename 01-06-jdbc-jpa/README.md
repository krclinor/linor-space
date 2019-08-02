# JPA
JPA로 다대다의 관계를 표현하기 위해 악기테이블을 추가하여 스키마를 구성하여 작업한다.

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
#### Singer 엔터티 클래스(일대다, 다대다 관계)
가수 엔터티 클래스를 생성한다.  
가수 엔터티는 앨범 엔터티와 일대다, 악기 엔터티와 다대다의 관계이다.  
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

#### Album 엔터티 클래스(다대일 관계)
앨범 엔터티는 가수 엔터티와 다대일 관계이다.    
```java
@Entity
//@Table(name = "album")
@Data
public class Album implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    //@Column(name = "release_date")
    private LocalDate releaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Singer singer;

    @Version
    private int version;
}
```
@ManyToOne은 Singer와 다대1을 의미한다.  
@JoinColumn은 외부키를 정의한다.  
@JoinColumn(name="SINGER_ID")은 SINGER_ID를 외부키로 사용함을 나타낸다.  
※ Lombok를 사용하여 개발하는 경우 두 엔터티의 관계에서 하나는 @ToString.Exclude어노테이션을 추가하여야 한다.  
그렇지 않으면 toString메서드가 무한루프에 빠져 오류가 발생한다. @EqualsAndHashCode.Exclude또한 마찬가지 이다.
   
#### Instrument 엔터티 클래스(다대다 관계)
악기 엔터티는 가수 엔터티와 다대다 관계이다.    
```java
@Entity
//@Table(name = "instrument")
@Data
public class Instrument implements Serializable{
    @Id
    //@Column(name = "instrument_id")
    private String instrumentId;

    @ManyToMany
    @JoinTable(name = "singer_instrument", joinColumns = @JoinColumn(name = "instrument_id"), inverseJoinColumns = @JoinColumn(name = "singer_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Singer> singers = new HashSet<>();
}
```
@ManyToMany는 다대다 매핑을 의미한다.  
@JoinTable은 다대다 매핑에 사용되는 테이블을 정의한다.  
@JoinTable(name="singer_instrument", joinColumns = @JoinColumn(name="SINGER_ID"), 
inverseJoinColumns=@JoinColumn(name="INSTRUMENT_ID"))은 SINGER_INSTRUMENT이라는 
조인테이블에 칼럼이 SINGER_ID이고 상대 조인컬럼은 INSTRUMENT_ID임을 나타낸다.


#### SingerSummary 엔터티 클래스(사용자 정의 결과 타입)
JPQL을 통해 생성한 쿼리에 사용할 사용자 정의 엔터티를 생성한다.    
```java
@Data
@AllArgsConstructor
public class SingerSummary implements Serializable {
    private String firstName;
    private String lastName;
    private String lastAlbum;
}
```
@AllArgsConstructor어노테이션은 클래스 내의 모든 프로퍼티를 매개변수로 하는 생성자를 만든다.  

### DAO인터페이스 구현클래스 생성
```java
@Transactional
@Repository
@Slf4j
public class SingerDaoImpl implements SingerDao {
    
    @PersistenceContext
    private EntityManager entityManager;
```
@Transactional은 데이터베이스 트랜잭션을 처리하기 위하여 설정한다.  
@Repository는 Persistency레이어에서 빈을 정의하기 위해 사용하는 어노테이션이다.  
인스턴스 생성시 EntityManager를 주입할 수 있도록 @Autowired어노테이션을 설정한다. 

#### findAll 메서드 구현(단순 쿼리)
```java
    @Override
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        return entityManager.createQuery("from Singer s", Singer.class).getResultList();
    }
```
EntityManager.createQuery()를 호출하여 쿼리를 생성하고 Query.getResultList()를 호출하여 결과 목록을 받아온다.  
단일 레코드만 조회하려면 Query.uniqueResult()를 호출한다.  
여기에서 사용되는 쿼리는 JPQL(Java Persistence Query Language)이며, 
JPA스팩의 일부로 정의된 플랫폼 독립적인 객체지향 쿼리 언어이다.  

#### findByFirstName 메서드 구현(NamedQuery)
NamedQuery를 사용하기 위해 먼저 엔터티 클래스에서 NamedQuery를 작성한다.
```java
@NamedQueries({
    @NamedQuery(name="Singer.findByFirstName",
    query="select distinct s from Singer s \n"
            + "where s.firstName = :firstName")
})
@Data
public class Singer implements Serializable{
```
엔터티 클래스에서 작성한 NamedQuery를 다음과 같이 사용한다.  
파라미터는 앞에 콜론(:)을 붙인다.
```java
    @Override
    public List<Singer> findByFirstName(String firstName) {
        return entityManager.createNamedQuery("Singer.findByFirstName", Singer.class)
                .setParameter("firstName", firstName)
                .getResultList();
    }
```
EntityManager.createNamedQuery()를 이용하여 호출한다.  
Name 파라미터 설정은 Query.setParameter(), 또는 Query.setParameterList()를 사용하여 설정한다.    
단일 레코드를 리턴하기 위해 Query.uniqueResult()를 사용하고, 여러 레코드를 리터하려면 Query.getResultList()를 사용한다.  

#### insert 메서드 구현
```java
    @Override
    public void insert(Singer singer) {
        entityManager.persist(singer);
    }
```
EntityManager.persist()를 호출하여 insert를 처리한다. Id가 없는 경우 생성된 id가 singer.id에 주입된다.  

#### update 메서드 구현
```java
    @Override
    public void update(Singer singer) {
        entityManager.merge(singer);
    }
```
EntityManager.merge()를 호출하여 데이타를 update한다.  

#### delete 메서드 구현
```java
    @Override
    public void delete(Integer singerId) {
        Singer singer = entityManager.createNamedQuery("Singer.findById",Singer.class).
                setParameter("id", singerId).getSingleResult();
        if(singer != null) {
            entityManager.remove(singer);
        }
    }
```
EntityManager.remove()를 호출하여 레코드를 삭제한다.    

#### listAllSingersSummary 메서드 구현(클래스 타입이 없는 결과 쿼리)
```java
    @Override
    public List<SingerSummary> listAllSingersSummary() {
        List<SingerSummary> result = entityManager.createQuery("select \n"
                + "new com.linor.singer.domain.SingerSummary(\n"
                + "s.firstName, s.lastName, a.title) from Singer s\n"
                + "left join s.albums a\n"
                + "where a.releaseDate=(select max(a2.releaseDate)\n"
                + "from Album a2 where a2.singer.id = s.id)", SingerSummary.class)
                .getResultList();
        return result;
    }
```
JPQL에서 명시적으로 컬럼을 지정하면 JPA는 Iterator<Object[]>를 리턴한다.  
JQPL은 select다음에 new 키워드와 함께 사용자정의 도메인 클래스를 선언하고, 생성자의 매개변수에 조회결과를 대입한다.    

#### findAllByNativeQuery 메서드 구현(Native SQL)
```java
    private static final String ALL_SINGER_NATIVE_SQL =
            "select id, first_name, last_name, birth_date, version from singer";
    @Override
    public List<Singer> findAllByNativeQuery() {
        return entityManager.createNativeQuery(ALL_SINGER_NATIVE_SQL, Singer.class)
                .getResultList();
    }
```
JPQL이 아닌 SQL문을 직접 사용하려면 EntityManager.createNativeQuery()메서드를 사용하여 처리한다.  


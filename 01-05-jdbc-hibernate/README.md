# Hibernate Session
Hibernate Session을 이용하여 Dao인터페이스를 구현해 본다.  
데이타베이스는 postgresql과 h2 둘 다 되는 시스템을 구현해 본다.   
  
## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
todo 프로젝트 의존성 라이브러리에 jpa와 hibernate-types-52를 추가한다.  

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
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Hibernate CamelCase를 SnakeCase로 변경 -->
        <dependency>
            <groupId>com.vladmihalcea</groupId>
            <artifactId>hibernate-types-52</artifactId>
            <version>2.9.9</version>
        </dependency>
    </dependencies>
```
추가한 h2는 내장 데이타베이스로 h2데이타베이스와 postgresql데이타베이스에서 테스트하기 위해 추가한다. 

### 어플리케이션 설정
hibernate관련 설정을 추가한다.  

소스 : [application.yml](src/main/resources/application.yml)
#### 프로파일 설정
```yml
#사용 프로파일 설정
spring.profiles.active: [postgres, dev]
```
프로파일은 여러개를 설정할 수 있다.  
사용 프로파일을 postgres와 dev를 설정한다.  
h2데이타베이스로 테스트하려면 postgres를 h2로 변경한다.  
개발용으로 테스트자료를 로딩하기 위해 dev프로파일을 등록하여 사용한다.  

#### postgreSql 데이타베이스용 데이타 소스 및 JPA 설정
```yml
#데이타소스
spring: 
  profiles: postgres
  datasource:
    platform: postgresql
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://postgres:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: never

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        physical-naming-strategy: com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy
        format_sql: true
        use_sql_comments: true
        jdbc.lob.non_contextual_creation: true
```
profiles에 postgres를 선언하여 액티브 프로파일에 postgres가 지정되면 여기에 선언된 데이타소스를 생성한다.  
platform을 postgresql로 설정하여, initialization-mode가 always이면 어플리케이션 시작시 
schema-postgresql.sql과 data-postgresql.sql 스크립트를 실행하도록 한다. 여기서는 initialization-mode를 never로 설정하고 AppStartupRunner를 이용하여 데이타를 생성한다.  
데이타베이스 스키마 생성을 스크립트로 하려면 다음에 나오는 ddl-auto를 none으로 설정해야 한다.  

테이블 생성은 ddl-auto를 create로 설정하여 Hibernate가 테이블을 생성하도록 설정한다.    
ddl-auto는 시스템 시작시 스키마 생성 규칙을 정의하는 것으로 create, create-update, update, none중 하나를 등록한다.
- create : 기존에 존재하면 drop하고 테이블을 새로 생성한다.
- update : 기존에 존재하면 modify하고 없으면 새로 생성한다.
- create-drop : 기존에 존재하면 drop하고 테이블을 새로 생성하며, 시스템 종료시 drop한다.  
- none : 스키마 작업을 하지 않는다.  

dialect는 사용하는 데이타베이스가 postgresql이므로 org.hibernate.dialect.PostgreSQLDialect를 설정한다.  
physical-naming-strategy에 Camel Case로 작성된 객체의 맴버변수를 Snake Case로 작성된 테이블 칼럼과 매핑될 수 있도록 
CamelCaseToSnakeCaseNamingStrategy로 설정한다.  
postgresql을 사용하는 경우 발생하는 오류를 제거하기 위해 jdbc.lob.non_contextual_creation을 true로 설정한다.(현재는 오류가 발생하지 않아 코멘트 처리함)    
show_sql을 true로 설정하면  hibernate가 생성한 sql문을 볼 수 있고,    
format_sql을 true로 설정하면 sql문을 읽기 쉽도록 만들어 준다.  
use_sql_comments를 true로 설정하면 sql문에 HQL쿼리를 주석으로 같이 보여준다.  

#### h2 데이타 소스 및 JPA 설정
```yml
#데이타소스
spring:
  profiles: h2
  datasource:
    platform: h2 
#    driver-class-name: org.h2.Driver
#    url: jdbc:h2:mem:test;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
#    username: sa
#    password: ''
#    initialization-mode: always
#  h2.console.enabled: true

  jpa:
#    show-sql: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate: 
        dialect: org.hibernate.dialect.H2Dialect
#        format_sql: true
#        use_sql_comments: true
        physical-naming-strategy: com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy
```
드라이버 클래스를 설정하지 않으면 스프링은 디폴트로 h2데이타베이스 드라이버를 설정한다.  
dialect는 사용하는 데이타베이스가 h2이므로 org.hibernate.dialect.H2Dialect를 설정한다.  

### 엔터티 클래스 생성
도메인 클래스라 불리던 클래스가 JPA에서는 주로 엔터티라고 부른다.  

#### Singer 엔터티 클래스(일대다, 다대다 관계)
가수 엔터티 클래스를 생성한다.  
가수 엔터티는 앨범 엔터티와 일대다, 악기 엔터티와 다대다의 관계이다.  

소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)
```java
@Entity
@Table(name="singer", uniqueConstraints = {@UniqueConstraint(name = "singer_uq_01", columnNames = {"firstName", "lastName"})})
@NamedQueries({
	@NamedQuery(name="Singer.findById",
			query="select distinct s from Singer s "
					+ "left join fetch s.albums a "
					+ "left join fetch s.instruments i "
					+ "where s.id = :id"),
	@NamedQuery(name="Singer.findAllWithAlbum",
			query="select distinct s from Singer s \n"
					+ "left join fetch s.albums a \n"
					+ "left join fetch s.instruments i"),
	@NamedQuery(name="Singer.findByFirstName",
			query="select distinct s from Singer s \n"
					+ "where s.firstName = :firstName"),
	@NamedQuery(name="Singer.findByFirstNameAndLastName",
			query="select distinct s from Singer s \n"
					+ "where s.firstName = :firstName\n"
					+ "and s.lastName = :lastName")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Singer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//@Column(name="first_name")
	@Column(length = 60)
	private String firstName;
	
	@Column(length = 60)
	private String lastName;
	
	//@Column(name="birth_date")
	private LocalDate birthDate;
	
	@OneToMany(mappedBy="singer", cascade=CascadeType.ALL, orphanRemoval=true)
	//@ToString.Exclude
	//@EqualsAndHashCode.Exclude
	private Set<Album> albums;

	@ManyToMany
	@JoinTable(name="singer_instrument", 
		joinColumns=@JoinColumn(name="singer_id",foreignKey = @ForeignKey(name="fk_singer_instrument_fk_01")),
		inverseJoinColumns=@JoinColumn(name="instrument_id",foreignKey = @ForeignKey(name="fk_singer_instrument_fk_02")))
	//@ToString.Exclude
	//@EqualsAndHashCode.Exclude
	private Set<Instrument> instruments;
	
	@Version
	private int version;
	
}
```
@Entity는 해당 클레스가 엔터티 클래스임을 표시한다.  
@Table은 매핑될 데이터베이스 테이블명을 설정한다.  
@Table(name = "singer")은 데이터베이스의 SINGER테이블과 매핑한다. 클래스명과 테이블 명이 동일할 경우 생략 가능하다.  
2개 이상의 컬럼으로 유일키를 생성하기 위해 uniqueConstraints속성을 이용한다. 
@UniqueConstraint 속성
- name : 유일키 명칭
- columnNames : 유일키에 사용할 클래스의 맴버변수들을 배열로 등록한다.  

@Id는 주키를 표시한다.  
@GeneratedValue는 주키 값의 생성 전략을 명시하는데 사용한다.  
선택 속성으로 generator와 strategy가 있다.  
- strategy는 persistence provider가 엔티티의 주키를 생성할 때 사용해야 하는 주키생성 전략을 의미한다.  
디폴트 값은 AUTO이다.  
- generator는 SequenceGenerator나 TableGenerator 어노테이션에서 명시된 주키 생성자를 재사용할 때 쓰인다.  
주키 생성 전략으로 JPA가 지원하는 것은 아래의 네 가지이다.  
- AUTO : (persistence provider가) 특정 DB에 맞게 자동 선택  
- IDENTITY : DB의 identity 컬럼을 이용  
- SEQUENCE : DB의 시퀀스 컬럼을 이용  
- TABLE : 유일성이 보장된 데이터베이스 테이블을 이용

@Column은 매핑할 테이블의 칼럼을 설정한다.  
@Column(name="first_name")는 데이터베이스 테이블의 칼럼이 first_name이다.  
application.yml설정에서 physical-naming-strategy를 설정하였기 때문에 @Column어노테이션을 사용하지 않더라도 
firstName프로퍼티는 first_name칼럼으로 매핑된다(CamelCase -> SnakeCase).  

@OneToMany는 1대다 관계를 표현하기 위해 사용한다.  
mappedBy="singer"는 Album클래스에서 Singer를 나타내는 맴버변수이다.   
@OneToMany 프로퍼티  
- targetEntity : 연결을 맺는 상대 엔티티
- fetch : 관계 엔티티의 읽기 전략을 설정(EAGER, LAZY)
- cascade : 현재 엔터티의 변경에 대해 관련 엔터티에 대한 변경 전략을 정의(ALL, PERSIST, MERGE, REMOVE, REFRESH, DETACH)
- mappedBy : 양뱡향 관계에서 주체가 되는 쪽(Many쪽, 외래키가 있는 쪽)을 정의
- orphanRemoval : 연관 관례에 있는 엔티티에서 변경이 일어난 경우 DB 변경을 같이 할지 결정. cascade는 JPA 레이어의 정의이고 orphanRemoval은 DB레이어에서 직접 처리한다. 기본은 false

@ManyToMany는 다대다 매핑을 의미한다.  
@JoinTable은 다대다 매핑에 사용되는 테이블을 정의한다.  
@JoinTable(name="singer_instrument", joinColumns = @JoinColumn(name="SINGER_ID"), 
inverseJoinColumns=@JoinColumn(name="INSTRUMENT_ID"))은 SINGER_INSTRUMENT이라는 
조인테이블에 칼럼이 SINGER_ID이고 상대 조인컬럼은 INSTRUMENT_ID임을 나타낸다.

@Version은 엔티티가 수정될 때 자동으로 값을 하나씩 증가한다. 엔티티를 수정할 때 조회 시점의 버전과 수정 시점의 버전이 다르면 예외가 발생한다.  
예를 들어 트랜잭션 1이 조회한 엔티티를 수정하고 있는데 트랜잭션 2에서 같은 엔티티를 수정하고 커밋해서 버전이 증가해버리면 트랜잭션 1이 커밋할 때 버전 정보가 다르므로 예외가 발생한다.

#### Album 엔터티 클래스(다대일 관계)
앨범 엔터티는 가수 엔터티와 다대일 관계를 표현한다.    

소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)
```java
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "album_uq_01", columnNames = {"singer_id", "title"})})
@NamedQueries({
	@NamedQuery(name="Album.findAlbumsBySinger",
			query="select a from Album a "
					+ "where a.singer.id = :singer_id"),
	@NamedQuery(name="Album.findByTitle",
			query="select a from Album a "
					+ "where a.title like :title||'%'")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100)
	private String title;

	//@Column(name = "release_date")
	private LocalDate releaseDate;

	@ManyToOne
	@JoinColumn(name = "singer_id", foreignKey = @ForeignKey(name="album_fk_01"))
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Singer singer;

	@Version
	private int version;
}
```
@ManyToOne은 Singer와 다대일을 의미한다.  
@JoinColumn은 외부키를 정의한다.  
@JoinColumn(name="SINGER_ID")은 SINGER_ID를 외부키로 사용함을 나타낸다.  
※ Lombok를 사용하여 개발하는 경우 두 엔터티의 관계에서 하나는 @ToString.Exclude어노테이션을 추가하여야 한다.  
그렇지 않으면 toString()이 무한루프에 빠져 오류가 발생한다.  @EqualsAndHashCode.Exclude또한 마찬가지 이다.
   
#### Instrument 엔터티 클래스(다대다 관계)
악기 엔터티는 가수 엔터티와 다대다 관계이다.
    
소스 : [Instrument.java](src/main/java/com/linor/singer/domain/Instrument.java)
```java
@Entity
//@Table(name = "instrument")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Instrument{
	@Id
	//@Column(name = "instrument_id")
	private String instrumentId;

//	@ManyToMany
//	@JoinTable(name = "singer_instrument", joinColumns = @JoinColumn(name = "instrument_id"), inverseJoinColumns = @JoinColumn(name = "singer_id"))
//	@ToString.Exclude
//	@EqualsAndHashCode.Exclude
//	@Singular
//	private Set<Singer> singers = new HashSet<>();
//	
//	public boolean addSinger(Singer singer) {
//		return getSingers().add(singer);
//	}
//	public void removeSinger(Singer singer) {
//		getSingers().remove(singer);
//	}
}
```
악기는 가수와 다대다 매핑이지만 악기를 이용하여 가수를 조회할 필요가 없는 경우 악기에서는 다대다를 표현할 필요는 없다.  

### DAO인터페이스 구현클래스 생성
SingerDao인터페이스를 Hibernate가 제공하는 Session을 이용하여 구현한다.  
소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/hibernate/SingerDaoImpl.java)
```java
@Transactional
@Repository
@Slf4j
public class SingerDaoImpl implements SingerDao {
    
    @PersistenceContext
    EntityManager entityManager;

    protected Session getCurrentSession()  {
        return entityManager.unwrap(Session.class);
    }
    
```
@Transactional은 선언적인 트랜잭션을 사용하기 위하여 설정한다.  
@Repository는 Persistency레이어용 빈으로 선언한다.  
인스턴스 생성시 EntityManager를 주입할 수 있도록 @Autowired어노테이션을 설정한다. 
entityManager를 이용하여 현재 세션을 가져오는 getCurrentSession메서드를 구현한다.

#### findAll 메서드 구현
```java
    @Override
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        Session session = getCurrentSession();
        return session
                .createQuery("from Singer")
                .list();
    }
```
entityManager에서 현재 세션을 session에 할당한다.  
session으로 쿼리문을 실행한다. 여기서 사용되는 쿼리문은 Hibernate Query Language(HQL)로 데이터베이스의 SQL문과 다르다.  
"from Singer s"는 JPQL문 "select s from Singer s"와 동일하다.  

#### findByFirstName 메서드 구현(NamedQuery)
NamedQuery를 사용하기 위해 먼저 엔터티 클래스에서 NamedQuery를 작성한다.  
Singer 엔터티 클래스에 선언한 내용이다.
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
        Session session = getCurrentSession();
        return session
                .getNamedQuery("Singer.findByFirstName")
                .setParameter("firstName", firstName)
                .list();
    }
```
session의 getNamedQuery메서드를 이용하여 호출한다.  
Name 파라미터 설정은 Query.setParameter(), 또는 Query.setParameterList()를 사용하여 설정한다.    
단일 레코드를 리턴하기 위해 Query.uniqueResult()를 사용하고, 여러 레코드를 리턴하려면 Query.list()를 사용한다.  

#### insert 메서드 구현
```java
    @Override
    public void insert(Singer singer) {
        Session session = getCurrentSession();
        session.saveOrUpdate(singer);
        log.info("저장된 가수 ID: " + singer.getId());
    }
```
Session.saveOrUpdate()를 호출하여 insert와 update처리를 모두 한다. Id가 없는 경우 생성된 id가 singer.id에 주입된다.  

#### update 메서드 구현
```java
    @Override
    public void update(Singer singer) {
        Session session = getCurrentSession();
        session.update(singer);
    }
```
Session.update()를 호출하거나 Session.saveOrUpdate()를 호출하여 데이타를 update한다.  

#### delete 메서드 구현
```java
    @Override
    public void delete(Integer singerId) {
        Session session = getCurrentSession();
        Singer singer = (Singer)session
                .getNamedQuery("Singer.findById")
                .setParameter("id", singerId)
                .uniqueResult();
        if(singer != null) {
            session.delete(singer);
        }
    }
```
Session.delete()를 호출하여 레코드를 삭제한다.    

## 결과 테스트
### 초기데이타 로딩 
개발용 시작시 초기데이타를 로딩한다.   

소스 : [AppStartupRunner.java](src/main/java/com/linor/singer/config/AppStartupRunner.java)
```java
@Profile("dev")
@Component
public class AppStartupRunner implements ApplicationRunner {
	@Autowired
	private SingerDao singerDao;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		Instrument instrument = new Instrument("기타");
		singerDao.insertInstrument(instrument);
		
		instrument = new Instrument("피아노");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument("드럼");
		singerDao.insertInstrument(instrument);

		instrument = new Instrument("신디사이저");
		singerDao.insertInstrument(instrument);

		Singer singer = Singer.builder()
				.firstName("종서")
				.lastName("김")
				.birthDate(LocalDate.parse("1970-12-09"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		Album album = Album.builder()
				.title("아름다운 구속")
				.releaseDate(LocalDate.parse("2019-01-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		album = Album.builder()
				.title("날개를 활짝펴고")
				.releaseDate(LocalDate.parse("2019-02-01"))
				.singer(singer)
				.build();
		
		singer.getAlbums().add(album);

		instrument = new Instrument("기타");
		singer.getInstruments().add(instrument);
		instrument = new Instrument("피아노");
		singer.getInstruments().add(instrument);
		
		singerDao.insertWithAlbum(singer);
		
		singer = Singer.builder()
				.firstName("건모")
				.lastName("김")
				.birthDate(LocalDate.parse("1999-07-12"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		album = Album.builder()
				.title("황혼의 문턱")
				.releaseDate(LocalDate.parse("2019-03-01"))
				.singer(singer)
				.build();

		singer.getAlbums().add(album);

		instrument = new Instrument();
		instrument.setInstrumentId("기타");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);

		singer = Singer.builder()
				.firstName("용필")
				.lastName("조")
				.birthDate(LocalDate.parse("1978-06-28"))
				.albums(new HashSet<Album>())
				.instruments(new HashSet<Instrument>())
				.build();
		
		instrument = new Instrument("드럼");
		singer.getInstruments().add(instrument);

		singerDao.insertWithAlbum(singer);
		
		singer = Singer.builder()
				.firstName("진아")
				.lastName("태")
				.birthDate(LocalDate.parse("2000-11-01"))
				.build();
		singerDao.insertWithAlbum(singer);
	}

}
```
@Profile("dev") : 설정에서 액티브 프로파일이 dev를 포함된는 경우 실행되도록 한다.  

빈으로 등록하기 위해 @Component를 선언한다.  

ApplicationRunner인터페이스를 상속받아 run메서드를 구현한다.  

### Junit 테스팅
Junit으로 SingerDaoTests를 실행한다.

## 정리
Hibernate는 OR매핑툴 중에서 가장 많이 사용되고 있다.  
장점은 엔터티 클래스에 테이블 및 컬럼정의만 해 놓으면 별도의 sql문 없이도 많은 처리가 가능하다.  
따라서 데이터베이스 벤더의 영향을 받지 않으면서 시스템 개발이 가능하다.
단점은 hibernate가 생성한 sql문이 우리가 원하는 sql문이 아닐 수도 있기 때문에 세심한 체크가 필요하다.  
Hibernate는 엔터티클래스를 이용하여 테이블을 생성할 수 있지만 가능하면 sql스크립트를 만들어서 사용하는 것이 좋다.  
테이블 스페이스 등 각종 물리적 스키마 내역을 다루기 어렵기 때문이다.




# Mybatis와 JPA를 동시 구현
기존 Mybatis와 jpa프로젝트를 혼합하여 구현한다.  
1번 데이타소스는 Mybatis가 사용하고, 2번 데이타소스는 JPA가 사용하도록 한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
소스 : [pom.xml](pom.xml)
```xml
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
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

		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.1.0</version>
		</dependency>
```
데이타베이스는 postgreSql을 사용한다.  

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```xml
spring.profiles.active: dev

spring:
  datasource:
    initialization-mode: always

#Multi DataSource 설정
db:
  db1: 
    datasource:
      driverClassName: org.postgresql.Driver
      jdbcUrl: jdbc:postgresql://localhost:5432/spring?currentSchema=singer
      username: linor
      password: linor1234
      
  db2: 
    datasource:
      driverClassName: org.postgresql.Driver
      jdbcUrl: jdbc:postgresql://localhost:5432/spring?currentSchema=public
      username: linor
      password: linor1234
```
동일 데이타베이스에 대하여 스키마를 달리한 2개의 데이타소스를 설정한다.  
db.db1의 데이타소스는 singer스키마를 사용하고, db.db2의 데이타소스는 public스키마를 사용하도록 한다.  
이 설정값들은 스프링이 알아서 사용할 수 없기 때문에 이 값들을 이용하여 데이타소스를 정의하는 설정 빈을 생성해야 한다.  

### 1번 데이타소스 및 Mybatis 설정
소스 : [Datasource1Config.java](src/main/java/com/linor/singer/config/Datasource1Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
@MapperScan(basePackages = {"com.linor.singer.dao1"}, sqlSessionFactoryRef = "sqlSessionFactory1")
public class Datasource1Config {
```
@Configuration으로 설정클래스임을 정의한다.  
@MapperScan은 Mybatis의 매퍼패키지와 사용할 sqlSessionFactory를 등록한다.  
- basePackages : 매퍼 인터페이스가 위치하는 자바 패키지를 등록한다.
- sqlSessionFactoryRef : Mybatis가 사용할 sessionFactory빈을 등록한다.

#### Datasource 빈 설정
```java
    @Bean
    @ConfigurationProperties("db.db1.datasource")
    @Primary
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }
```
@Bean으로 해당 메서드가 빈임을 선언한다. name값을 지정하지 않으면 메서드 명이 name값으로 선언된다.    
@ConfigurationProperties("db.db1.datasource")로 application.yml에 등록한 db.db1.datasource하위 프로퍼티들을 가져오도록 한다.   
@Primary는 동일타입의 빈이 여러게 있을 경우 기본으로 사용할 빈을 설정한다.  

#### SqlSessionFactory 빈 설정
```java
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage("com.linor.singer.domain1");
        sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath*:/**/dao1/*.xml"));
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.setConfiguration(configuration);

        return sqlSessionFactory.getObject();
    }
```
@Bean으로 선언한 메서드의 파라미터는 해당 타입의 빈이 존재할 경우 스피링이 알아서 파라미터 값을 주입한다. 
SessionFactory 빈 등록으로 빈의 명칭은 sqlSessionFactory1로 @Primary를 선언하여 디폴트 SessionFactory 빈으로 선언한다.  
데이타 소스는 위에서 정의한 dataSource1을 주입한다. dataSource1이 @Primary로 선언되어 있으므로 @Qualifier("dataSource1")어노테이션을 
사용하지 않더라고 dataSource1이 주입된다.  

SqlSessionFactory의 setDataSource()로 사용할 데이타소스를 설정한다.  
setTypeAliasesPackage()를 설정하여 도메인사용시 패키지명을 사용하지 않고도 도메인을 지정할 수 있다.  
예) com.linor.singer.domain.Album -> Album  
setMapperLocations()로 매퍼인터페이스를 처리할 xml 매퍼파일의 위치를 설정한다.  
applicationContext.getResources("classpath*:/**/dao1/*.xml")는 classpath 내에 dao1을 포함하는 
폴더에 존재하는 xml파일들을 찾아서 설정한다.  
Configuration.setMapUnderscoreToCamelCase()값을 true로 설정하여 테이블 컬럼의 snake case를 camel case로 변환하여 
ORM매핑처리를 한다.  
예) FIRST_NAME -> firstName

#### 트랜잭션관리 빈 설정
```java
    @Primary
    @Bean
    public DataSourceTransactionManager txManager1(@Qualifier("dataSource1") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
```
트랜잭션 관리를 위한 빈을 설정한다.  

#### 데이타베이스 초기화 빈 설정
```java
    @Bean
    public DataSourceInitializer dataSourceInitializer1(@Qualifier("dataSource1") DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post1.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data-post1.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(datasource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
```
application.yml의 datasource.initialization-mode가 always인 경우 클래스 패스의 루트에 있는 schema-post1.sql과 data-post1.sql스크립트를 
처리하도록 한다.  

### 2번 데이타소스 및 JPA EntityManager 설정
소스 : [Datasource2Config.java](src/main/java/com/linor/singer/config/Datasource2Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
public class Datasource2Config {
```
@Configuration으로 설정클래스임을 정의한다.  

#### Datasource 빈 설정
```java
	@Bean(name = "dataSource2")
	@ConfigurationProperties("db.db2.datasource")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}
```
@Bean으로 해당 메서드가 빈임을 선언한다. name값을 지정하지 않으면 메서드 명이 name값으로 선언된다.    
@ConfigurationProperties("db.db2.datasource")로 application.yml에 등록한 db.db2.datasource하위 프로퍼티들을 가져오도록 한다. 

#### EntityManagerFactory 빈 설정
```java
	@Bean(name = "entityManagerFactory2")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory2(
			EntityManagerFactoryBuilder builder,
			@Qualifier("dataSource2") DataSource dataSource) {

		Map<String, ?> jpaProperties = hibernateProperties();

		return builder
				.dataSource(dataSource)
				.packages("com.linor.singer.domain2")
				.persistenceUnit("db2")
				.properties(jpaProperties)
				.build();
	}

	private Map<String, ?> hibernateProperties() {
		
		Map<String, Object> hibernateProp = new HashMap<>();

		hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibernateProp.put("hibernate.hbm2ddl.auto", "create");
		hibernateProp.put("hibernate.format_sql", "false");
		hibernateProp.put("hibernate.use_sql_comments", "false");
		hibernateProp.put("hibernate.show_sql", "true");
		hibernateProp.put("hibernate.physical_naming_strategy", "com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy");
		hibernateProp.put("hibernate.jdbc.lob.non_contextual_creation", "true");
		hibernateProp.put("hibernate.enable_lazy_load_no_trans", "true");
		return hibernateProp;
	}
```
@Bean으로 선언한 메서드의 파라미터는 해당 타입의 빈이 존재할 경우 스피링이 알아서 파라미터 값을 주입한다. 
EntityManagerFactory 빈 등록으로 빈의 명칭은 entityManagerFactory2로 선언한다.  
데이타 소스는 위에서 정의한 dataSource2를 주입한다.   

EntityManagerFactoryBuilder는 빌더클래스로 설정값을 설정한 다음 마지막에 build()로 EntityManagerFactory를 생성한다.
dataSource()는 사용할 데이타소스를 선언한다.  
packages()는 엔터티가 존재하는 패키지를 선언한다.  
persistenceUnit()는 PersistentUnit명을 선언한다.  
properties()는 하이버네이트 추가 설정사항을 선언한다. 

#### 트랜잭션관리 빈 설정
```java
	@Bean(name = "transactionManager2")
	public PlatformTransactionManager transactionManager2(
			@Qualifier("entityManagerFactory2") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
```
트랜잭션 관리를 위한 빈을 설정한다.  
JPA에서는 JpaTransactionManager를 이용하여 트랜잭션을 처리한다.  

## Domain 클래스 생성
### Mybatis용 
소스 : [Singer1.java](src/main/java/com/linor/singer/domain1/Singer1.java)  
소스 : [Album1.java](src/main/java/com/linor/singer/domain1/Album1.java)  

### JPA용 
소스 : [Singer2.java](src/main/java/com/linor/singer/domain2/Singer2.java)  
소스 : [Album2.java](src/main/java/com/linor/singer/domain2/Album2.java)  
소스 : [Instrument2.java](src/main/java/com/linor/singer/domain2/Instrument2.java)  
소스 : [SingerSummary2.java](src/main/java/com/linor/singer/domain1/SingerSummary2.java)  

## DAO인터페이스 생성
### 1번 Mybatis용 
소스 : [SingerDao1.java](src/main/java/com/linor/singer/dao1/SingerDao1.java)  
### 2번 JPA용 
소스 : [SingerDao2.java](src/main/java/com/linor/singer/dao2/SingerDao2.java)  

## SingerDao인터페이스 구현
### 1번 Mybatis용 
소스 : [SingerDao1.xml](src/main/resources/com/linor/singer/dao1/SingerDao1.xml)  

### 2번 JPA용 
소스 : [SingerDao2Impl.java](src/main/java/com/linor/singer/jpa2/SingerDao2Impl.java)  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.  
1번 데이타소스용 테스트 [SingerDaoTests1.java](src/test/java/com/linor/singer/SingerDaoTests1.java)  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class SingerDaoTests1 {
```  
SingerDaoTest1테스트케이스는 Mybatis용을 테스트 한다.  
트랜잭션을 처리하기 위해 @Transactional어노테이션을 사용한다. 사용할 트랜잭션 명을 지정하지 않으면 Primary로 지정한 트랜잭션을 사용한다.  
Mybatis용 트랜잭션이 Primary로 지정되어 있기 때문에 Mybatis용 트랜잭션을 사용한다.  

2번 데이타소스용 테스트 [SingerDaoTests2.java](src/test/java/com/linor/singer/SingerDaoTests2.java)  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional("transactionManager2")
public class SingerDaoTests2 {
```
SingerDaoTests2테스트케이스는 JPA를 테스트한다.  
@Transactional어노테이션에 transactionManager2 트랜잭션 매니저를 선언하여 JPA용 트랜잭션 매니저를 사용하도록 지정한다.  
 
1,2번 둘 다 테스트 [SingerDaoTests3.java](src/test/java/com/linor/singer/SingerDaoTests3.java)  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingerDaoTests3 {
	@Autowired
	private SingerDao1 singerDao1;

	@Autowired
	private SingerDao2 singerDao2;
```
SingerDaoTests3테스트케이스는 Mybatis와 JPA를 둘 다 사용한다.  
@Transacitonal어노테이션에 트랜잭션매니저를 지정하지 않았으므로 기본은 transactionManager1인 Mybatis용 트랜잭션매니저가 사용된다.  
```java
	@Test
	@Transactional("transactionManager2")
	public void test207InsertSinger() {
		log.info("테스트207");
		Singer2 singer = new Singer2();
		singer.setFirstName("조한");
		singer.setLastName("김");
		singer.setBirthDate(LocalDate.parse("1990-10-16"));
		singerDao2.insert(singer);
		List<Singer2> singers = singerDao2.findAll();
		log.info(">>> 김조한 추가후");
		listSingers2(singers);
	}
```
JPA용 트랜잭션매니저가 필요한 경우 메서드의 상단에 @Transactional어노테이션으로 트랜잭션매니저를 지정해 준다.  

## 정리
데이타 소스 각각을 별개로 사용할 경우 트랜잭션에 문가 발생하지 않으나, 2개의 데이타소스를 동시에 사용하는 경우 Primary로 지정하지 않은 2번 데이타소스의 
트랜잭션은 롤백되지 않는 단점이 있다.  
이를 해결하려면 JTA트랜잭션을 사용해야 한다.  
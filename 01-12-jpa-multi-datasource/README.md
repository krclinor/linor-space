# JPA에서 여러 데이타소스를 통한 구현
기존 jpa프로젝트를 2개의 데이타소스를 만들어서 구현해 본다.  

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
jpa 프로젝트 와 동일.  
소스 : [pom.xml](pom.xml)

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```xml
spring.profiles.active: dev

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

### 1번 데이타소스 및 JPA EntityManager 설정
소스 : [Datasource1Config.java](src/main/java/com/linor/singer/config/Datasource1Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
public class Datasource1Config {
```
@Configuration으로 설정클래스임을 정의한다.  

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

#### EntityManagerFactory 빈 설정
```java
    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource1") DataSource dataSource) {
        Map<String, ?> jpaProperties = hibernateProperties();
        return builder
                .dataSource(dataSource)
                .packages("com.linor.singer.domain1")
                .persistenceUnit("db1")
                .properties(jpaProperties)
                .build();
    }
    private Map<String, ?> hibernateProperties() {
        
        Map<String, Object> hibernateProp = new HashMap<>();
        hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProp.put("hibernate.hbm2ddl.auto", "create");
        hibernateProp.put("hibernate.format_sql", "false");
        hibernateProp.put("hibernate.use_sql_comments", "false");
        hibernateProp.put("hibernate.show_sql", "false");
        hibernateProp.put("hibernate.physical_naming_strategy", "com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy");
        hibernateProp.put("hibernate.jdbc.lob.non_contextual_creation", "true");
        return hibernateProp;
    }
```
@Bean으로 선언한 메서드의 파라미터는 해당 타입의 빈이 존재할 경우 스피링이 알아서 파라미터 값을 주입한다. 
EntityManagerFactory 빈 등록으로 빈의 명칭은 entityManagerFactory로 @Primary를 선언하여 기본 EntityManagerFactory 빈으로 선언한다.  
데이타 소스는 위에서 정의한 dataSource1을 주입한다. dataSource1이 @Primary로 선언되어 있으므로 @Qualifier("dataSource1")어노테이션을 
사용하지 않더라고 dataSource1이 주입된다.  

EntityManagerFactoryBuilder는 빌더클래스로 설정값을 설정한 다음 마지막에 build()로 EntityManagerFactory를 생성한다.
dataSource()는 사용할 데이타소스를 선언한다.  
packages()는 엔터티가 존재하는 패키지를 선언한다.  
persistenceUnit()는 PersistentUnit명을 선언한다.  
properties()는 하이버네이트 추가 설정사항을 선언한다. 

#### 트랜잭션관리 빈 설정
```java
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
```
트랜잭션 관리를 위한 빈을 설정한다.  

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
@Bean으로 해당 메서드가 빈임을 선언한다. name값을 지정하지 않으면 메서드명이 name값으로 선언된다.    
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
		hibernateProp.put("hibernate.show_sql", "false");
		hibernateProp.put("hibernate.physical_naming_strategy", "com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy");
		hibernateProp.put("hibernate.jdbc.lob.non_contextual_creation", "true");
		hibernateProp.put("hibernate.enable_lazy_load_no_trans", "true");
		return hibernateProp;
	}```
@Bean으로 선언한 메서드의 파라미터는 해당 타입의 빈이 존재할 경우 스피링이 알아서 파라미터 값을 주입한다. 
EntityManagerFactory 빈 등록으로 빈의 명칭은 entityManagerFactory2로 선언한다.  
데이타 소스는 위에서 정의한 dataSource2을 주입한다. 데이타소스가 2개이므로 @Qualifier("dataSource1")어노테이션을 
사용하여 dataSource2를 주입한다.  

#### 트랜잭션관리 빈 설정
```java
	@Bean(name = "transactionManager2")
	public PlatformTransactionManager transactionManager2(
			@Qualifier("entityManagerFactory2") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
```
트랜잭션 관리를 위한 빈을 설정한다.  

## Domain 클래스 생성
기존 jpa 프로젝트의 엔터티를 엔터티1, 엔터티2로 만든다.    
### 1번 데이타소스용 
소스 : [Singer1.java](src/main/java/com/linor/singer/domain1/Singer1.java)  
소스 : [Album1.java](src/main/java/com/linor/singer/domain1/Album1.java)  
소스 : [Instrument1.java](src/main/java/com/linor/singer/domain1/Instrument1.java)  
소스 : [SingerSummary1.java](src/main/java/com/linor/singer/domain1/SingerSummary1.java)  

### 2번 데이타소스용 
소스 : [Singer2.java](src/main/java/com/linor/singer/domain2/Singer2.java)  
소스 : [Album2.java](src/main/java/com/linor/singer/domain2/Album2.java)  
소스 : [Instrument2.java](src/main/java/com/linor/singer/domain2/Instrument2.java)  
소스 : [SingerSummary2.java](src/main/java/com/linor/singer/domain1/SingerSummary2.java)  

## DAO인터페이스 생성
기존 jpa 프로젝트의 DAO인터페이스를 2개로 복사하여 만든다.      
### 1번 데이타소스용 
소스 : [SingerDao1.java](src/main/java/com/linor/singer/dao1/SingerDao1.java)  
### 2번 데이타소스용 
소스 : [SingerDao2.java](src/main/java/com/linor/singer/dao2/SingerDao2.java)  

## SingerDao인터페이스 구현
기존 jpa 프로젝트의 DAO인터페이스 구현을 2개로 복사하여 만든다.      
### 1번 데이타소스용 
소스 : [SingerDao1Impl.java](src/main/java/com/linor/singer/jpa1/SingerDao1Impl.java)  

### 2번 데이타소스용 
소스 : [SingerDao2Impl.java](src/main/java/com/linor/singer/jpa2/SingerDao2Impl.java)  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.  
1번 데이타소스용 테스트 [SingerDaoTests1.java](src/test/java/com/linor/singer/SingerDaoTests1.java)  
2번 데이타소스용 테스트 [SingerDaoTests2.java](src/test/java/com/linor/singer/SingerDaoTests2.java)  
1,2번 둘 다 테스트 [SingerDaoTests3.java](src/test/java/com/linor/singer/SingerDaoTests3.java)  

## 정리
데이타 소스 각각을 별개로 사용할 경우 트랜잭션에 문가 발생하지 않으나, 2개의 데이타소스를 동시에 사용하는 경우 Primary로 지정하지 않은 2번 데이타소스의 
트랜잭션은 롤백되지 않는 단점이 있다.  
이를 해결하려면 Mybatis와 마찬가지로 JTA트랜잭션을 사용해야 한다.  
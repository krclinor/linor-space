# Mybatis + atomikos JTA를 이용한 멀티 데이타소스 트랜잭션
datasource-multi-mybatis 프로젝트에서 데이타소스를 atomikos JTA로 변경하여 2개의 데이타소스를 트랜잭션 처리한다.   

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
소스 : [pom.xml](pom.xml)
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
        </dependency>       
```
datasource-multi-mybatis 프로젝트에 atomikos를 추가한다.  

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```xml
spring:
  datasource:
    initialization-mode: always
  jta:
    enabled: true
    
#Multi DataSource 설정
db:
  db1: 
    datasource:
      unique-resource-name: dataSource1
      max-pool-size: 5
      min-pool-size: 1
      max-life-time: 20000
      borrow-connection-timeout: 10000
      xa-data-source-class-name: org.postgresql.xa.PGXADataSource
      xa-properties:
        user: linor
        password: linor1234
        URL: jdbc:postgresql://postgres:5432/spring?currentSchema=singer
  db2: 
    datasource:
      unique-resource-name: dataSource2
      max-pool-size: 5
      min-pool-size: 1
      max-life-time: 20000
      borrow-connection-timeout: 10000
      xa-data-source-class-name: org.postgresql.xa.PGXADataSource
      xa-properties:
        user: linor
        password: linor1234
        URL: jdbc:postgresql://postgres:5432/spring?currentSchema=public
```
spring.jta.enabled를 true로 설정하여 JTA를 사용하도록 한다.  
db1과 db2의 데이타소스를 atomiko에 맞게 설정한다.  
xa-data-source-class-name에 JTA용 데이타베이스 드라이버로 설정한다. postgresql의 경우 org.postgresql.xa.PGXADataSource이다.  

### 1번 데이타소스 및 Mybatis 설정
소스 : [Datasource1Config.java](src/main/java/com/linor/singer/config/Datasource1Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
@MapperScan(basePackages = {"com.linor.singer.dao1"}, sqlSessionFactoryRef = "sqlSessionFactory1")
public class Datasource1Config {
```
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  

#### Datasource 빈 설정
```java
    @Bean
    @ConfigurationProperties("db.db1.datasource")
    @Primary
    public DataSource dataSource1() {
        return new AtomikosDataSourceBean();
    }
```
Datasource빈을 AtomikosDataSourceBean으로 생성한다.  

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
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  

#### 트랜잭션관리 빈 설정
트랜잭션 관리를 위한 빈은 스프링이 알아서 JTA 트랜잭션 매니저로 설정하므로 별도로 설정하지 않늗다.  

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
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  

### 2번 데이타소스 및 Mybatis 설정
소스 : [Datasource2Config.java](src/main/java/com/linor/singer/config/Datasource2Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
@MapperScan(basePackages = {"com.linor.singer.dao2"}, sqlSessionFactoryRef = "sqlSessionFactory2")
public class Datasource2Config {
```
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  

#### Datasource 빈 설정
```java
    @Bean
    @ConfigurationProperties("db.db2.datasource")
    public DataSource dataSource2() {
        return new AtomikosDataSourceBean();
    }
```
datasource-multi-mybatis 프로젝트와 내용중 데이타소스만 AtomikosDataSourceBean로 생성한다.  

#### SqlSessionFactory 빈 설정
```java
    @Bean
    public SqlSessionFactory sqlSessionFactory2(@Qualifier("dataSource2") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage("com.linor.singer.domain2");
        sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath*:/**/dao2/*.xml"));;
        //sqlSessionFactory.setTransactionFactory(new SpringManagedTransactionFactory());
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sqlSessionFactory.setConfiguration(configuration);

        return sqlSessionFactory.getObject();
    }
```
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  
 
#### 트랜잭션관리 빈 설정
1번 데이타소스와 같이 설정하지 않는다.    

#### 데이타베이스 초기화 빈 설정
```java
    @Bean
    public DataSourceInitializer dataSourceInitializer2(@Qualifier("dataSource2") DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema-post2.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data-post2.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(datasource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
```
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  

### 데이타베이스 초기화 파일 생성
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  
#### 1번 데이타소스용 
소스 : [schema-post1.sql](src/main/resources/schema-post1.sql)  
소스 : [data-post1.sql](src/main/resources/data-post1.sql)  

#### 2번 데이타소스용 
소스 : [schema-post2.sql](src/main/resources/schema-post2.sql)  
소스 : [data-post2.sql](src/main/resources/data-post2.sql)  

## Domain 클래스 생성
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  
### 1번 데이타소스용 
소스 : [Singer1.java](src/main/java/com/linor/singer/domain1/Singer1.java)  
소스 : [Album1.java](src/main/java/com/linor/singer/domain1/Album1.java)  

### 2번 데이타소스용 
소스 : [Singer2.java](src/main/java/com/linor/singer/domain2/Singer2.java)  
소스 : [Album2.java](src/main/java/com/linor/singer/domain2/Album2.java)  

## DAO인터페이스 생성
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  
### 1번 데이타소스용 
소스 : [SingerDao1.java](src/main/java/com/linor/singer/dao1/SingerDao1.java)  

### 2번 데이타소스용 
1번과 내용은 동일하다.  
소스 : [SingerDao2.java](src/main/java/com/linor/singer/dao2/SingerDao2.java)  

## SingerDao인터페이스 구현
datasource-multi-mybatis 프로젝트와 내용이 동일하다.  
### 1번 데이타소스용 
소스 : [SingerDao1.xml](src/main/resources/com/linor/singer/dao1/SingerDao1.xml)  

### 2번 데이타소스용 
소스 : [SingerDao2.xml](src/main/resources/com/linor/singer/dao2/SingerDao2.xml)  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.  
소스 : [SingerDaoTests.java](src/test/java/com/linor/singer/SingerDaoTests.java)  

## 정리
JTA를 이용하면 여러 데이타베이스의 트랜잭션을 묶어서 처리할 수 있다.  

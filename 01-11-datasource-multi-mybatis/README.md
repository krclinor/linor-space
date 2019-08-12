# Mybatis에서 여러 데이타소스를 통한 구현
기존 mybatis프로젝트를 2개의 데이타소스를 만들어서 구현해 본다.  

singer와 public스키마로 2개의 데이타소스를 만들고, 각 스키마에 singer와 album테이블을 만든다.  
기존 mybatis에서 만든 SingerDao인터페이스를 SingerDao1, SingerDao2로 만들어서 각 데이타소스에서 구현한다.    

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
mybatis 프로젝트 와 동일.  
소스 : [pom.xml](pom.xml)

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```xml
  datasource:
    initialization-mode: always
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
datasource.initialization-mode를 always로 하여 스키마 생성과 데이타 로드를 스크립트 파일로 처리하도록 한다.  
동일 데이타베이스에 대하여 스키마를 달리한 2개의 데이타소스를 설정한다.  
db.db1의 데이타소스는 singer스키마를 사용하고, db.d2의 데이타소스는 public스키마를 사용하도록 한다.  
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
SessionFactory 빈 등록으로 빈의 명칭은 sqlSessionFactory1로 @Primary를 선언하여 디폴트 SessionFactor 빈으로 선언한다.  
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


### 2번 데이타소스 및 Mybatis 설정
소스 : [Datasource2Config.java](src/main/java/com/linor/singer/config/Datasource2Config.java) 

#### 클래스 어노테이션 설정
```java
@Configuration
@MapperScan(basePackages = {"com.linor.singer.dao2"}, sqlSessionFactoryRef = "sqlSessionFactory2")
public class Datasource2Config {
```
1번 데이타소스 설정과 같고 다른점은 basePackage는 com.linor.singer.dao2, sqlSessionFactoryRef 빈은 sqlSessionFactory2를 
사용하도록 한다.  

#### Datasource 빈 설정
```java
    @Bean
    @ConfigurationProperties("db.db2.datasource")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }
```
@ConfigurationProperties("db.db2.datasource")로 application.yml에 등록한 db.db2.datasource하위 프로퍼티들을 가져오도록 한다. 
@Primary를 선언하지 않았으므로 이 데이타소스를 사용하려면 @Qualifier("dataSource2")를 사용해야 한다.  

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
1번 데이타베이스의 SqlSessionFactory와 적용방법은 같고, 사용할 DataSource, TypeAliasesPackage, MapperLocations의 
값만 다르게 설정한다.  
 
#### 트랜잭션관리 빈 설정
```java
    @Bean(name="txManager2")
    public DataSourceTransactionManager dataSourceTransactionManager2(@Qualifier("dataSource2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
```
2번 데이타소스의 트랜잭션 관리를 위한 빈을 설정한다.  

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
2번 데이타소스의 초기화 스크립트 파일을 지정한다.    

### 데이타베이스 초기화 파일 생성
기존 mybatis프로젝트의 schema.sql, data.sql과 같음.  
#### 1번 데이타소스용 
소스 : [schema-post1.sql](src/main/resources/schema-post1.sql)  
소스 : [data-post1.sql](src/main/resources/data-post1.sql)  

#### 2번 데이타소스용 
소스 : [schema-post2.sql](src/main/resources/schema-post2.sql)  
소스 : [data-post2.sql](src/main/resources/data-post2.sql)  

## Domain 클래스 생성
기존 mybatis프로젝트의  
Singer.java -> Singer1.java, Singer2.java  
Album.java -> Album1.java, Album2.java  
#### 1번 데이타소스용 
소스 : [Singer1.java](src/main/java/com/linor/singer/domain1/Singer1.java)  
소스 : [Album1.java](src/main/java/com/linor/singer/domain1/Album1.java)  

#### 2번 데이타소스용 
소스 : [Singer2.java](src/main/java/com/linor/singer/domain2/Singer2.java)  
소스 : [Album2.java](src/main/java/com/linor/singer/domain2/Album2.java)  

## DAO인터페이스 생성

소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  
```java
@Mapper
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
todo프로젝트에서 만든 dao인터페이스에 @Mapper어노테이션을 추가하여 매퍼용 인터페이스임을 알린다.  
해당 인터페이스는 Mybatis Mapper에서 XML로 구현할 수도 있고, 현 인터페이스에 어노테이션으로 추가할 수 있다.  

## SingerDao인터페이스 구현
Mybatis Mapper인터페이스를 XML파일로 구현한다.  
eclipse Marketplace에서 Mybatipse플러그인을 설치하면 Mybatis Mapper xml파일을 쉽게 생성할 수 있다.  
소스 : [SingerDao.xml](src/main/resources/com/linor/singer/dao/SingerDao.xml)  

### Mapper 선언부
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.linor.singer.dao.SingerDao">
```
처음 2줄은 무조건 추가한다.  
namespace에 구현할 인터페이스명을 등록한다.

### findAll 메서드 구현
```xml
<select id="findAll" resultType="Singer">
    select * from singer
</select>
```
select는 쿼리 작업을 수행하는 데 사용하며, id는 인터페이스의 메서드에 해당하며 메서드 명과 동일해야 한다.  
sql문을 실행 후 칼럼명을 snake case에서 camel case로 변환하여 Singer객체의 각 프로퍼티에 매핑하여 
배열객체(List<Singer>)로 리턴한다.

### findAllWithAllbums 메서드 구현
#### 방법1. 중첩 select를 이용한 조회
```xml
<resultMap type="Singer" id="singerWithAlbumMap">
    <collection property="albums" ofType="Album"
        column="id" select="selectAlbumsForSinger"/>
</resultMap>
<select id="findAllWithAlbums" resultMap="singerWithAlbumMap">
    select * from singer
</select>
<select id="selectAlbumsForSinger" parameterType="int" resultType="Album">
    select *
    from    album
    where   singer_id = #{id}
</select>
```
앨범을 포함한 가수 목록을 조회하고, resultMap에서 가수에 대한 앨범목록 가져오기 위하여 
collection에서 select속성에 앨범을 조회하는 selectAlbumsForSinger를 등록한다.

#### 방법2. 중첩 Result를 이용한 조회
```xml
<resultMap type="Singer" id="singerWithAlbumMap2">
    <id property="id" column="id"/>
    <result property="firstName" column="first_name"/>
    <result property="lastName" column="last_name"/>
    <result property="birthDate" column="birth_date"/>
    <collection property="albums" ofType="Album">
        <id property="id" column="album_id"/>
        <result property="singerId" column="singer_id"/>
        <result property="title" column="title"/>
        <result property="releaseDate" column="release_date"/>
    </collection>
</resultMap>
<select id="findAllWithAlbums" resultMap="singerWithAlbumMap2">
    select s.id, s.first_name, s.last_name, s.birth_date,
            a.id album_id, a.singer_id, a.title, a.release_date
    from    singer s
    left outer join album a on a.singer_id = s.id
</select>
```

### findNameById 메서드 구현(명명된 파라미터)
```xml
<select id="findNameById" parameterType="int" resultType="string">
    select s.first_name ||' '|| s.last_name as name
    from    singer s
    where   s.id = #{id}
</select>
```
sql문에서 사용하는 파라미터는 #{ }로 감싸서 표현한다. 파라미터의 타입은 parameterType에 선언하며 클래스도 가능하다.

### findByFirstName 메서드 구현(다이나믹 sql문)
```xml
<select id="findByFirstName" parameterType="string" resultMap="singerWithAlbumMap2">
    select s.id, s.first_name, s.last_name, s.birth_date,
            a.id album_id, a.singer_id, a.title, a.release_date
    from    singer s
    left outer join album a on a.singer_id = s.id
    <where>
        <if test="value != null">
        s.first_name = #{value}
        </if>
    </where>
</select>
```
파라미터 값이 null인 경우 where절이 사라져서 findAllWithAlbums메서드와 동일하게 처리되고, 파라미터에 값이 존재하면 where절이 
만들어진다.  
파라미터가 단일인 변수명은 value가 디폴트이다.(다르게 명명해도 상관 없음) value값에 따라 sql 문이 달라진다.  
value에 값이 없을 경우 findAll과 동일한 효과가 나타난다.

### insert 메서드 구현
```xml
<insert id="insert" parameterType="Singer"
    useGeneratedKeys="true"
    keyProperty="id">
    insert into singer (first_name, last_name, birth_date)
    values(#{firstName}, #{lastName}, #{birthDate})
</insert>
```
sql insert문 실행시 자동으로 생성되는 id값을 받아오기 위해 useGeneratedKeys를 true로 설정하고, keyProperty를 id로 설정한다.  
insert문 실행 후 mybatis가 singer객체의 id에 값을 대입한다. 

### update 메서드 구현
```xml
<update id="update" parameterType="Singer">
    update singer
    set     first_name = #{firstName},
            last_name = #{lastName},
            birth_date = #{birthDate}
    where   id = #{id}
</update>
```

### delete 메서드 구현
```xml
<update id="delete" parameterType="int">
    delete from singer
    where   id = #{id}
</update>
```

### insertWithAlbum 메서드 구현(plsql처리)
```xml
<insert id="insertWithAlbum" parameterType="Singer">
    <selectKey keyProperty="id" resultType="int" order="BEFORE">
        Select nextval(pg_get_serial_sequence('singer', 'id'))
    </selectKey>
    insert into singer(id, first_name, last_name, birth_date)
    values(#{id}, #{firstName}, #{lastName}, #{birthDate});
    <if test="albums != null">
        <foreach collection="albums" item="album">
            insert into album (singer_id, title, release_date)
            values (#{id}, #{album.title}, #{album.releaseDate});
        </foreach>
    </if>
</insert>
```
주요 sql문을 처리하기 전에 sql문을 처리할 수 있는 selectKey를 제공한다.  
selectKey에서 처리 후 결가 값을 keyProperty에 선언한 id에 저장하는에 이 id는 Singer클래스의 프로퍼티로 선언되어 있어야 한다.  
order를 BEFORE로 선언함으로써 주 쿼리 실행전에 처리하도록 한다.   

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.

## 정리
Mybatis는 전자정부프레임워크에서 Persistence레이어로 사용하고 있다.  
SQL문을 잘 다루는 개발자에게 적합하고, 모든 SQL문을 별도의 공간에서 관리할 수 있어 편리하다.  
단점은 SQL문을 개발자가 직접 구현해야 하며, 데이터베이스가 바뀔 경우 해당 데이타베이스에 맞게 SQL문을 수정해 주어야 한다.


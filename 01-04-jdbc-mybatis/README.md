# Mybatis를 이용한 구현
SingerDao인터페이스를 Mybatis로 구현한다.  

마이바티스 참조 URL : http://www.mybatis.org

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
todo 프로젝트 의존성 라이브러리에 mybatis-spring-boot-starter를 추가한다.  
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
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>
    </dependencies>
```
## 설정
소스 : [application.yml](src/main/resources/application.yml)  

### 데이타 소스 설정
todo 프로젝트와 동일하게 설정한다.  

### Mybatis 설정
```yml
#마이바티스
mybatis:
  mapper-locations: classpath*:/**/dao/*.xml
  type-aliases-package: com.linor.singer.domain
  configuration.map-underscore-to-camel-case: true
```
mapper-locations는 sql문을 처리하는 mybatis mapper xml파일의 위치를 지정한다.  
type-aliases-package를 등록하면 도메인사용시 패키지명을 사용하지 않고도 도메인을 지정할 수 있다.  
예) com.linor.singer.domain.Album -> Album  
configuratioins.map-underscore-to-camel-case를 true로 설정하면 테이블 컬럼의 snake case를 camel case로 변환하여 
ORM매핑처리를 한다.  
예) FIRST_NAME -> firstName

### 데이타베이스 초기화 파일 생성
todo 프로젝트와 동일하게 설정한다.  

소스 : [schema.sql](src/main/resources/schema.sql)  
소스 : [data.sql](src/main/resources/data.sql)  

## Domain 클래스 생성
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  

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
select는 쿼리 작업을 수행하는 데 사용하며, id는 인터페이스의 메서드에 해당하며 매서드 명과 동일해야 한다.  
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
앨범을 포함한 가수 목록을 조회하기 위해 resultMap에서 가수에 대한 앨범목록 가져오기 위하여 
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
    begin;
        insert into singer(id, first_name, last_name, birth_date)
        values(#{id}, #{firstName}, #{lastName}, #{birthDate});
        <if test="albums != null">
            <foreach collection="albums" item="album">
                insert into album (singer_id, title, release_date)
                values (#{id}, #{album.title}, #{album.releaseDate});
            </foreach>
        </if>
    end;
</insert>
```
주요 sql문을 처리하기 전에 sql문을 처리할 수 있는 selectKey를 제공한다.  
selectKey에서 처리 후 결가 값을 keyProperty에 선언한 id에 저장하는에 이 id는 Singer클래스의 프로퍼티로 선언되어 있어야 한다.  
order를 BEFORE로 선언함으로써 주 쿼리 실행전에 처리하도록 한다.  
plsql을 처리하기 위해서는 begin end;블록으로 감싸서 처리한다.  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.

## 정리
Mybatis는 전자정부프레임워크에서 Persistence레이어를 담당하고 있어 중요하고 좋은 도구이다.  
SQL문을 잘 다루는 개발자에게 적합하고, 모든 SQL문을 별도의 저장공간에서 관리할 수 있어 편리하다.  
단점은 SQL문을 개발자가 직접 구현해야 하며, 데이터베이스가 바뀔 경우 해당 데이타베이스에 맞게 SQL문을 수정해 주어야 한다.

 
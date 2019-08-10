# Spring JPA Repository
스프링이 제공하는 JpaRepository인터페이스를 이용하여 개발해 본다.  

## Spring Boot Starter를 이용한 프로젝트 생성
### 의존성 라이브러리
Hibernate 프로젝트와 동일하게 설정한다.  
소스 : [pom.xml](pom.xml)
### 어플리케이션 설정
hibernate프로젝트와 동일하게 설정한 다음 아래 내용을 수정한다  .    
소스 : [application.yml](src/main/resources/application.yml)
```yml
  jpa:
#    show-sql: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        physical-naming-strategy: com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy
        #format_sql: true
        #use_sql_comments: true
        jdbc.lob.non_contextual_creation: true
        enable_lazy_load_no_trans: true #일대다 매핑에서 fetch를 Lazy로하는 경우 오류 막음
        #temp.use_jdbc_metadata_default: false
```
enable_lazy_load_no_trans를 true로 설정하여 엔터티 매핑의 
fetch 속성의 디폴트 값인 Lazy모드에 발생하는 오류를 막는다.  

### 엔터티 클래스 생성
생성한 Singer, Album, Instrument 엔터티 및 SingerSummary 도메인 클래스 jpa 프로젝트와 동일하다.  
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  
소스 : [Instrument.java](src/main/java/com/linor/singer/domain/Instrument.java)  
소스 : [SingerSummary.java](src/main/java/com/linor/singer/domain/SingerSummary.java)

### SingerRepository 인터페이스 생성
소스 : [SingerRepository.java](src/main/java/com/linor/singer/repository/SingerRepository.java)
```java
public interface SingerRepository extends JpaRepository<Singer, Integer> {
    List<Singer> findAll();
    List<Singer> findByFirstName(String firstName);
    List<Singer> findByFirstNameAndLastName(String firstName, String lastName);
    List<Singer> findAllWithAlbum();
    
    @Query("select \n"
                + "new com.linor.singer.domain.SingerSummary(\n"
                + "s.firstName, s.lastName, a.title) from Singer s\n"
                + "left join s.albums a\n"
                + "where a.releaseDate=(select max(a2.releaseDate)\n"
                + "from Album a2 where a2.singer.id = s.id)")
    public List<SingerSummary> listAllSingersSummary();

    @Query(value = "select id, first_name, last_name, birth_date, version from singer", nativeQuery = true)
    public List<Singer> findAllByNativeQuery();
}
```
JpaRepository를 상속하여 클래스가 아닌 인터페이스를 생성한다. 이 인터페이스의 구현체는 스프링이 알아서 처리한다.  
이 때 엔터티와 주키를 제공해야 한다.  
JpaRepository는 기본적으로 다음 메서드를 제공한다.
<table>
    <thead>
        <tr>
            <th>method</th>
            <th>기능</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>save()</td>
            <td>레코드 저장 (insert, update)</td>
        </tr>
        <tr>
            <td>findOne()</td>
            <td>primary key로 레코드 한건 찾기</td>
       </tr>
        <tr>
            <td>findAll()</td>
            <td>전체 레코드 불러오기. 정렬(sort), 페이징(pageable) 가능</td>
        </tr>
        <tr>
            <td>count()</td>
            <td>레코드 갯수</td>
        </tr>
        <tr>
            <td>delete()</td>
            <td>레코드 삭제</td>
        </tr>
    </tbody>
</table>

또한 Query메소드를 작성할 수 있으며 다음 규칙에 따라 작성하여 스프링이 구현하도록 할 수 있다.    
<table>
    <tr>
        <th>메서드</th>
        <th>설명</th>
    </tr>
    <tr>
        <td>findBy로 시작</td>
        <td>쿼리를 요청하는 메서드임을 알림
    </tr>
    <tr>
        <td>countBy로 시작</td>
        <td>쿼리결과 레코드 수를 요청하는 메서드임을 알림</td>
    </tr>
</table>
findBy다음에는 Entity의 멤버변수를 입력하면 해당 필드를 검색하여 결과를 리턴한다.  
sql문의 where절을 메서드 이름으로 전달한다고 생각하면 된다.  

findBy나 countBy다음에 나올 쿼리 메서드에 포함할 수 있는 키워드는 다음과 같다. 
<table>
<thead>
<tr>
<th>Keyword</th>
<th>Sample</th>
<th>JPQL snippet</th>
</tr>
</thead>
<tbody>
<tr>
<td><p><code>And</code></p></td>
<td><p><code>findByLastnameAndFirstname</code></p></td>
<td><p><code>… where x.lastname = ?1 and x.firstname = ?2</code></p></td>
</tr>
<tr>
<td><p><code>Or</code></p></td>
<td><p><code>findByLastnameOrFirstname</code></p></td>
<td><p><code>… where x.lastname = ?1 or x.firstname = ?2</code></p></td>
</tr>
<tr>
<td><p><code>Is,Equals</code></p></td>
<td><p><code>findByFirstname</code>,<code>findByFirstnameIs</code>,<code>findByFirstnameEquals</code></p></td>
<td><p><code>… where x.firstname = ?1</code></p></td>
</tr>
<tr>
<td><p><code>Between</code></p></td>
<td><p><code>findByStartDateBetween</code></p></td>
<td><p><code>… where x.startDate between ?1 and ?2</code></p></td>
</tr>
<tr>
<td><p><code>LessThan</code></p></td>
<td><p><code>findByAgeLessThan</code></p></td>
<td><p><code>… where x.age &lt; ?1</code></p></td>
</tr>
<tr>
<td><p><code>LessThanEqual</code></p></td>
<td><p><code>findByAgeLessThanEqual</code></p></td>
<td><p><code>… where x.age ⇐ ?1</code></p></td>
</tr>
<tr>
<td><p><code>GreaterThan</code></p></td>
<td><p><code>findByAgeGreaterThan</code></p></td>
<td><p><code>… where x.age &gt; ?1</code></p></td>
</tr>
<tr>
<td><p><code>GreaterThanEqual</code></p></td>
<td><p><code>findByAgeGreaterThanEqual</code></p></td>
<td><p><code>… where x.age &gt;= ?1</code></p></td>
</tr>
<tr>
<td><p><code>After</code></p></td>
<td><p><code>findByStartDateAfter</code></p></td>
<td><p><code>… where x.startDate &gt; ?1</code></p></td>
</tr>
<tr>
<td><p><code>Before</code></p></td>
<td><p><code>findByStartDateBefore</code></p></td>
<td><p><code>… where x.startDate &lt; ?1</code></p></td>
</tr>
<tr>
<td><p><code>IsNull</code></p></td>
<td><p><code>findByAgeIsNull</code></p></td>
<td><p><code>… where x.age is null</code></p></td>
</tr>
<tr>
<td><p><code>IsNotNull,NotNull</code></p></td>
<td><p><code>findByAge(Is)NotNull</code></p></td>
<td><p><code>… where x.age not null</code></p></td>
</tr>
<tr>
<td><p><code>Like</code></p></td>
<td><p><code>findByFirstnameLike</code></p></td>
<td><p><code>… where x.firstname like ?1</code></p></td>
</tr>
<tr>
<td><p><code>NotLike</code></p></td>
<td><p><code>findByFirstnameNotLike</code></p></td>
<td><p><code>… where x.firstname not like ?1</code></p></td>
</tr>
<tr>
<td><p><code>StartingWith</code></p></td>
<td><p><code>findByFirstnameStartingWith</code></p></td>
<td><p><code>… where x.firstname like ?1</code> (parameter bound with appended <code>%</code>)</p></td>
</tr>
<tr>
<td><p><code>EndingWith</code></p></td>
<td><p><code>findByFirstnameEndingWith</code></p></td>
<td><p><code>… where x.firstname like ?1</code> (parameter bound with prepended <code>%</code>)</p></td>
</tr>
<tr>
<td><p><code>Containing</code></p></td>
<td><p><code>findByFirstnameContaining</code></p></td>
<td><p><code>… where x.firstname like ?1</code> (parameter bound wrapped in <code>%</code>)</p></td>
</tr>
<tr>
<td><p><code>OrderBy</code></p></td>
<td><p><code>findByAgeOrderByLastnameDesc</code></p></td>
<td><p><code>… where x.age = ?1 order by x.lastname desc</code></p></td>
</tr>
<tr>
<td><p><code>Not</code></p></td>
<td><p><code>findByLastnameNot</code></p></td>
<td><p><code>… where x.lastname &lt;&gt; ?1</code></p></td>
</tr>
<tr>
<td><p><code>In</code></p></td>
<td><p><code>findByAgeIn(Collection&lt;Age&gt; ages)</code></p></td>
<td><p><code>… where x.age in ?1</code></p></td>
</tr>
<tr>
<td><p><code>NotIn</code></p></td>
<td><p><code>findByAgeNotIn(Collection&lt;Age&gt; age)</code></p></td>
<td><p><code>… where x.age not in ?1</code></p></td>
</tr>
<tr>
<td><p><code>True</code></p></td>
<td><p><code>findByActiveTrue()</code></p></td>
<td><p><code>… where x.active = true</code></p></td>
</tr>
<tr>
<td><p><code>False</code></p></td>
<td><p><code>findByActiveFalse()</code></p></td>
<td><p><code>… where x.active = false</code></p></td>
</tr>
<tr>
<td><p><code>IgnoreCase</code></p></td>
<td><p><code>findByFirstnameIgnoreCase</code></p></td>
<td><p><code>… where UPPER(x.firstame) = UPPER(?1)</code></p></td>
</tr>
</tbody>
</table>

### DAO인터페이스 구현클래스 생성

소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/repository/SingerDaoImpl.java)
```java
@Repository
@Transactional
@Slf4j
public class SingerDaoImpl implements SingerDao {
    
    @Autowired
    private SingerRepository singerRepository;
    
    @Autowired
    private AlbumRespository albumRepository;
```
@Transactional은 데이터베이스 트랜잭션을 처리하기 위하여 설정한다.  
@Repository는 Persistency레이어에서 빈을 정의하기 위해 사용하는 어노테이션이다.  
JpaRepository를 상속받아 생성한 SingerRepository와 AlbumRespository를 @Autowired를 이용하여 주입한다.  

#### findAll 메서드 구현
```java
    @Override
    public List<Singer> findAll() {
        return singerRepository.findAll();
    }
```
JpaRepository가 제공하는 findAll()메서드를 바로 이용한다. 

#### findByFirstName 메서드 구현
```java
    @Override
    public List<Singer> findByFirstName(String firstName) {
        return singerRepository.findByFirstName(firstName);
    }
```
SingerRepository에 선언한 findByFirstName()메서드를 호출하기만 하면 된다.  

#### insert 메서드 구현
```java
    @Override
    public void insert(Singer singer) {
        singerRepository.save(singer);
    }
```
JpaRepository인터페이스에서 제공하는 save()메서드를 호출한다.  

#### update 메서드 구현
```java
    @Override
    public void update(Singer singer) {
        singerRepository.save(singer);
    }
```
insert()메서드와 로직이 동일하다.  

#### delete 메서드 구현
```java
    @Override
    public void delete(Integer singerId) {
        singerRepository.deleteById(singerId);
    }
```
JpaRepository에서 제공하는 deleteById()메서드를 호출한다.    

#### listAllSingersSummary 메서드 구현(클래스 타입이 없는 결과 쿼리)
먼저 SingerRepository에 다음 메서드를 선언한다.
```java
    @Query("select \n"
                + "new com.linor.singer.domain.SingerSummary(\n"
                + "s.firstName, s.lastName, a.title) from Singer s\n"
                + "left join s.albums a\n"
                + "where a.releaseDate=(select max(a2.releaseDate)\n"
                + "from Album a2 where a2.singer.id = s.id)")
    public List<SingerSummary> listAllSingersSummary();
```
findBy로 해결할 수 없는 쿼리는 JPQL을 이용하여 작성한다.  

```java
    @Override
    public List<SingerSummary> listAllSingersSummary() {
        return singerRepository.listAllSingersSummary();
    }
```
JPQL에서 명시적으로 컬럼을 지정하면 JPA는 Iterator<Object[]>를 리턴한다.  
JQPL은 select다음에 new 키워드와 함께 사용자정의 도메인 클래스를 선언하고, 생성자의 매개변수에 조회결과를 대입한다.    

#### findAllByNativeQuery 메서드 구현(Native SQL)
SingerRepository에 다음과 같에 메서드를 선언한다.
```java
    @Query(value = "select id, first_name, last_name, birth_date, version from singer", nativeQuery = true)
    public List<Singer> findAllByNativeQuery();
```
@Query에서 nativeQuery를 true로 설정하여 해당 쿼리가 sql문임을 선언한다.  

```java
    @Override
    public List<Singer> findAllByNativeQuery() {
        return singerRepository.findAllByNativeQuery();
    }
```
SingerRepository에서 선언한 findAllByNativeQuery()메서드를 호출한다.  

### 초기데이타 로딩 
개발용 시작시 초기데이타를 로딩한다.   
소스 : [AppStartupRunner.java](src/main/java/com/linor/singer/config/AppStartupRunner.java)

### Junit 테스팅
Junit으로 SingerDaoTests를 실행한다.

#### 주의사항
JpaRepository를 사용하는 경우 테스트케이스에 @Transactional어노테이션을 사용하지 말아야 한다.  
JpaRepository는 트랜잭션이 커밋될 때 update sql문을 생성하는 듯 하다.  
따라서 테스트케이스에서 트랜잭션을 사용하면 테스트가 종료되는 시점에 롤백이 발생하여 update sql문이 만들어 지지 않는다.  

소스 : [SingerDaoTests.java](src/test/java/com/linor/singer/SingerDaoTests.java)    
```java
@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
@Slf4j
public class SingerDaoTests {
```

## 정리
JpaRepository는 Spring에서 제공하는 인터페이스로 JPA를 좀더 쉽게 사용할 수 있도록 한다.
 

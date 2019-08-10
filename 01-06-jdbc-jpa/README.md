# JPA EntityManager
JPA는 EJB 3.0에서 ORM 기술에 대한 API 표준스펙이며, 이 스팩을 구현한 구현체가 Hibernate, OpenJPA, EclipseLink, 
TopLink등이 있다.  
JPA EntityManager를 이용하여 처리하는 방법을 배워본다.  

## Spring Boot Starter를 이용한 프로젝트 생성
### 의존성 라이브러리
Hibernate 프로젝트와 동일하게 설정.  
소스 : [pom.xml](pom.xml)

### 어플리케이션 설정
hibernate프로젝트와 동일하게 설정.    
소스 : [application.yml](src/main/resources/application.yml)

### 엔터티 클래스 생성
생성한 Singer, Album, Instrument 엔터티 클래스는 hibernate프로젝트에서 생성한 엔터티 클래스와 동일하다.  
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  
소스 : [Instrument.java](src/main/java/com/linor/singer/domain/Instrument.java)  

#### SingerSummary 엔터티 클래스(사용자 정의 결과 타입)
JPQL을 통해 생성한 쿼리에 사용할 사용자 정의 엔터티를 생성한다.    

커스텀 쿼리를 위해 다음 도메인 객체를 추가한다.  
소스 : [SingerSummary.java](src/main/java/com/linor/singer/domain/SingerSummary.java)
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
SingerDao인터페이스를 JPA EntityManager를 이용하여 구현한다.  
소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/jpa/SingerDaoImpl.java)  
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
@Transactional(readOnly=true)은 읽기전용 트랜잭션을 타도록 한다.  
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
                .setParameter("firtstName", firstName)
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
JPQL은 select다음에 new 키워드와 함께 사용자정의 도메인 클래스를 선언하고, 생성자의 매개변수에 조회결과를 대입한다.    

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

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.

## 정리
JPA는 표준스펙이므로 Hibernate뿐 아니라 다른 구현체로 대체가 가능하다.  

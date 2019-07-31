# Native JDBC
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 일반적인 JDBC프로그램으로 구현한다.  
구현한 SingerDaoImpl클래스를 분석해보자.

## SingerDao인터페이스 구현

### 클래스에 선언한 어노테이션
```java
@Slf4j
@Repository
@Transactional
public class SingerDaoImpl implements SingerDao {
```
@Slf4j는 로그를 위한어노테이션으로 lombok에서 제공한다.  
@Repository는 스프링이 제공하는 어노테이션으로 데이타베이스 저장소를 구현하기 위해 설정한다.  
@Transactional은 스프링이 제공하는 어노테이션으로 트랜젝션 관리용이다.  

### 데이타소스 선언 
```java
    @Autowired
    private DataSource dataSource;
``` 
 데이타소스를 선언하고 @Autowired어노테이션으로 스프링이 데이타소스를 주입하도록 한다.
  

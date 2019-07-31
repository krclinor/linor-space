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
  
### findAll 메서드 구현
```java
    @Override
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement("select * from singer");
            rs = stmt.executeQuery();
            List<Singer> singers = new ArrayList<>();
            
            while(rs.next()) {
                Singer singer = new Singer();
                singer.setId(rs.getInt("id"));
                singer.setFirstName(rs.getString("first_name"));
                singer.setLastName(rs.getString("last_name"));
                singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
                singers.add(singer);
                log.info("가수명 : {}{}, 생년월일: {}", singer.getLastName(), singer.getFirstName(),singer.getBirthDate().toString());
            }
            return singers;
        } catch (SQLException e) {
            log.error("에러코드: {}, 에러내역: {}", e.getErrorCode(), e.getMessage());
            return null;
        }finally {
            if(rs != null ) try {rs.close();}catch (Exception e2) {}
            if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
            if(con != null ) try {con.close();}catch (Exception e2) {}
        }
    }
```
@Override어노테이션은 인터페이스로 상속받은 메서드임을 표시한다.
@Transactional(readOnly=true)을 표기하여 트랜잭션을 읽기전용으로 설정하였다.
@Slf4j어노테이션을 설정하였기 때문에 log.info나 log.error메서드를 사용하여 로그를 뿌릴 수 있다.


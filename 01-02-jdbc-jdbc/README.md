# Native JDBC를 이용한 구현
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 일반적인 JDBC프로그램으로 구현한다.  

## SingerDao인터페이스 구현

### 클래스에 선언한 어노테이션
소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/jdbc/SingerDaoImpl.java)
```java
@Slf4j
@Repository
public class SingerDaoImpl implements SingerDao {
```
@Slf4j는 로그를 위한 어노테이션으로 lombok에서 제공한다.  
@Repository는 스프링이 제공하는 어노테이션으로 데이타베이스 저장소를 구현하기 위해 설정한다.  

### 데이타소스 선언 
```java
    @Autowired
    private DataSource dataSource;
``` 
 데이타소스를 선언하고 @Autowired어노테이션으로 스프링이 데이타소스를 주입하도록 한다.
  
### findAll 메서드 구현
```java
    @Override
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
PreparedStatement를 이용하여 "select * from singer"sql문을 실행한 후  
결과를 Singer클래스 객체에 담은 리스트객체를 리턴하는 메서드를 구현하였다.  
@Override어노테이션은 인터페이스로 상속받은 메서드임을 표시한다.  
@Slf4j어노테이션을 설정하였기 때문에 log.info나 log.error메서드를 사용하여 로그를 뿌릴 수 있다.  

### insert 메서드 구현
```java
    @Override
    public void insert(Singer singer) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement("insert into SINGER\n"+
                    "(first_name, last_name, birth_date)\n"+
                    "values(?, ?, ?)\n",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, singer.getFirstName());
            stmt.setString(2, singer.getLastName());
            stmt.setDate(3, Date.valueOf(singer.getBirthDate()));
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                singer.setId(rs.getInt(1));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(rs != null ) try {rs.close();}catch (Exception e2) {}
            if(stmt != null ) try {stmt.close();}catch (Exception e2) {}
            if(con != null ) try {con.close();}catch (Exception e2) {}
        }
    }
```
insert메서드는 매개변수로 받은 가수 객체를 데이타베이스에 인서트 하는 작업을 수행한다.  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.

## 정리
가장 다루기 힘든 처리방법이며, 스프링에서 제공하는 선언적 트랜잭션을 사용할 수 없어 트랜잭션을 수작업으로 관리해야 한다.  
사용한 Resultset, Statement, Connection은 메서드 종료전에 모두 닫아야 한다. 그렇지 않으면 메모리 누수가 발생할 수 있다.

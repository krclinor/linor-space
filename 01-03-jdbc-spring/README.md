# Spring JDBC
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 스프링 JDBC로 구현한다.  

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
#### 1. RowMapper클래스2를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from singer";
    
        return template.query(sql,new RowMapper<Singer>() {
            @Override
            public Singer mapRow(ResultSet rs, int rowNum) throws SQLException {
                Singer singer = new Singer();
                singer.setId(rs.getInt("id"));
                singer.setFirstName(rs.getString("first_name"));
                singer.setLastName(rs.getString("last_name"));
                singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
                return singer;
            }
        });
    }
```
#### 2. 람다 함수를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from singer";
    
        return template.query(sql, (rs, rowNum) -> {
            Singer singer = new Singer();
            singer.setId(rs.getInt("id"));
            singer.setFirstName(rs.getString("first_name"));
            singer.setLastName(rs.getString("last_name"));
            singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
            return singer;
        });
    }
```
#### 3. BeanPropertyRowMapper를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select * from singer";
    
        return template.query(sql, new BeanPropertyRowMapper<Singer>(Singer.class));
    }
```
1번째 RowMapper를 이용한 방법은 가장 일반적인 방법이지만 코딩이 약간 길다.  
2번째 람다함수를 이용한 방법은 RowMapper를 이용한 방법보다 코딩이 줄지만 자바 8이상에서만 가능하다.  
3번째 BeanPropertyRowMapper를 이용한 방법이 가장 단순하지만 성능면에서 위 2가지 방법보다 떨어질 수 있다.  


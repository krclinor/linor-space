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

JdbcTemplate의 query메서드는 여러 레코드인 배열객체를 리턴하고, queryForObject메서드는 단일 레코드인 단일객체를 리턴한다.   

### findAll 메서드 구현
#### 방법1. RowMapper클래스2를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
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
#### 방법2. 람다 함수를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
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
#### 방법3. BeanPropertyRowMapper를 이용한 방법
```java
    @Override   
    @Transactional(readOnly=true)
    public List<Singer> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String sql = "select * from singer";
    
        return template.query(sql, new BeanPropertyRowMapper<Singer>(Singer.class));
    }
```
방법1 RowMapper를 이용한 방법은 가장 일반적인 방법이지만 코딩이 약간 길다.  
방법2 람다함수를 이용한 방법은 RowMapper를 이용한 방법보다 코딩이 줄지만 자바 8이상에서만 가능하다.  
방법3 BeanPropertyRowMapper를 이용한 방법은 snake case를 camel case로 자동변환까지 가능한 쉽고, 단순하지만 성능면에서 위 2가지 방법보다 떨어질 수 있다.  

### findAllWithAlbums 메서드 구현
#### ResultSetExtractor를 이용한 중첩 도메인 오브젝트 추출
```java
    @Override
    @Transactional(readOnly=true)
    public List<Singer> findAllWithAlbums() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String sql = "select s.id, s.first_name, s.last_name, s.birth_date,\n" + 
                "   a.id album_id, a.title, a.release_date\n" + 
                "from   singer s\n" + 
                "left outer join album a on s.id = a.singer_id";
        return template.query(sql, new SingerWithAlbumExtractor());
    }

    private static final class SingerWithAlbumExtractor implements ResultSetExtractor<List<Singer>>{

        @Override
        public List<Singer> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, Singer> map = new HashMap<>();
            Singer singer;
            while(rs.next()) {
                Integer id = rs.getInt("id");
                singer = map.get(id);
                if(singer == null) {
                    singer = new Singer();
                    singer.setId(id);
                    singer.setFirstName(rs.getString("first_name"));
                    singer.setLastName(rs.getString("last_name"));
                    singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
                    singer.setAlbums(new ArrayList<>());
                    map.put(id, singer);
                }
                Integer albumId = rs.getInt("album_id");
                if(albumId > 0) {
                    Album album = new Album();
                    album.setId(albumId);
                    album.setSingerId(id);
                    album.setTitle(rs.getString("title"));
                    album.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    singer.getAlbums().add(album);
                }
            }
            return new ArrayList<>(map.values());
        }
    }
```
하나의 ResultSet을 하나의 도메인 오브젝트로 매핑하는 경우에는 RowMapper<T>를 사용하면 되지만 복잡한 도메인 오브젝트 매핑은 ResultSetExtractor사용한다. 

### findById 메서드 구현(파라미터 처리)
#### 방법1. 람다함수를 이용한 방법
```java
    @Override
    @Transactional(readOnly=true)
    public Singer findById(Integer id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "select * from singer where id = ?";
        return (Singer)jdbcTemplate.queryForObject(sql, new Object[] {id}, (rs, rowNum) -> {
            Singer singer = new Singer();
            singer.setId(rs.getInt("id"));
            singer.setFirstName(rs.getString("first_name"));
            singer.setLastName(rs.getString("last_name"));
            singer.setBirthDate(rs.getDate("birth_date").toLocalDate());
            return singer;
        });
    }
```
#### 방법2. BeanPropertyRowMapper를 이용한 방법
```java
    @Override
    @Transactional(readOnly=true)
    public Singer findById(Integer id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "select * from singer where id = ?";
        return (Singer)jdbcTemplate.queryForObject(sql, new Object[] {id}, new BeanPropertyRowMapper<Singer>(Singer.class));
    }
```
파라미터 값을 전달하기 위해 Object배열 객체를 사용한다.  

### findNameById 메서드 구현
#### 이름이 부여된 파라미터 처리
```java
    @Override
    @Transactional(readOnly=true)
    public String findNameById(Integer id) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String sql = "select first_name||' '||last_name from singer where id = :singer_id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("singer_id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);
    }
```
이름이 부여된 파라미터를 처리하기 위해서 sql문에 :(콜론)을 이용하여 파라미터에 이름을 부여한다.  
jdbcTemplate은 NamedParameterJdbctTemplate클래스로 생성한다.  
파라미터는 맵객체를 이용하여 전달한다.  

### insert 메서드 구현
#### SqlUpdate이용한 자료 수정
```java
    @Override
    public void insert(Singer singer) {
        InsertSinger insertSinger = new InsertSinger(dataSource);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("first_name", singer.getFirstName());
        paramMap.put("last_name", singer.getLastName());
        paramMap.put("birth_date", singer.getBirthDate());
        KeyHolder keyHolder =new GeneratedKeyHolder();
        insertSinger.updateByNamedParam(paramMap, keyHolder);
        singer.setId(keyHolder.getKey().intValue());
        log.info("추가된 가수ID: {}",singer.getId() );
    }
    private static final class InsertSinger extends SqlUpdate{
        private static final String sql = "insert into singer (first_name, last_name, birth_date)\n"+
                "values(:first_name, :last_name, :birth_date)";
        public InsertSinger(DataSource dataSource) {
            super(dataSource, sql);
            declareParameter(new SqlParameter("first_name", Types.VARCHAR));
            declareParameter(new SqlParameter("last_name", Types.VARCHAR));
            declareParameter(new SqlParameter("birth_date", Types.DATE));
            setGeneratedKeysColumnNames(new String[] {"id"});
            setReturnGeneratedKeys(true);
        }
    }
```

 
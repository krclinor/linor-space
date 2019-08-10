# Spring JDBC Template을 이용한 구현
Spring에서 제공하는 JdbcTemplate을 이용하여 SingerDao인터페이스를 구현한다.

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
todo 프로젝트와 동일하게 설정한다.  
소스 : [pom.xml](pom.xml)

### 데이타 소스 설정
todo 프로젝트와 동일하게 설정한다.  
소스 : [application.yml](src/main/resources/application.yml)  

### 데이타베이스 초기화 파일 생성
todo 프로젝트와 동일하게 설정한다.  

소스 : [schema.sql](src/main/resources/schema.sql)  
소스 : [data.sql](src/main/resources/data.sql)  

### Domain 클래스 생성
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  

### DAO인터페이스 생성
todo 프로젝트와 동일하게 구현.  
소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  

## SingerDao인터페이스 구현
소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/spring/SingerDaoImpl.java)  

### 클래스에 선언한 어노테이션
```java
@Slf4j
@Repository
@Transactional
public class SingerDaoImpl implements SingerDao {
```
@Slf4j는 로그를 위한 어노테이션으로 lombok에서 제공한다.  
@Repository는 스프링이 제공하는 어노테이션으로 데이타베이스 저장소(Persistence layer)를 구현하기 위해 설정한다.  
@Transactional은 스프링이 제공하는 어노테이션으로 트랜잭션을 사용함을 선언한다.  

### 데이타소스 선언 
```java
    @Autowired
    private DataSource dataSource;
``` 
 데이타소스를 선언하고 @Autowired어노테이션으로 스프링이 데이타소스를 주입하도록 한다.

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
ReadOnly트랜잭션을 타도록 하기 위해 @Transactional(readOnly=true)를 설정한다. 이렇게 하면 해당 메서드에서는   
insert,update,delete sql문을 사용할 수 없게 된다.

데이타소스를 파라미터로 전달하여 JdbcTemplate객체를 생성한다.  
JdbcTemplate.query()에 SQL문과, RowMapper인터페이스 구현객체를 생성하여 파라미터로 전달한다.  
JdbcTemplate.query()는 여러 레코드인 배열객체를 리턴하고, JdbcTemplate.queryForObject()는 단일 레코드인 단일객체를 리턴한다.   
RowMapper인터페이스 구현체로 mapRow()를 구현해서 사용하며, 매개변수로 ResultSet과, 레코드 번호가 넘어온다.  
SQL문에 의해 추출된 ResultSet값을 객체에 매핑하여 결과객체를 리턴한다.  

RowMapper를 이용한 방법은 가장 일반적인 방법이지만 코딩 길이가 약간 길다.  
#### 방법2. 람다 함수를 이용한 RowMapper 구현방법
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
이 방법은 코딩 길이가 줄지만 자바 8이상에서만 가능하다.
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
이 방법은 snake case를 camel case로 자동변환까지 가능한 쉽고, 단순하지만 성능면에서 위 2가지 방법보다 떨어질 수 있다.  

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
하나의 ResultSet을 하나의 도메인 객체로 매핑하는 경우에는 RowMapper<T>를 사용하면 되지만 복잡한 
도메인 객체 매핑은 ResultSetExtractor사용한다. 

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
파라미터 값을 전달하기 위해 Object배열 객체를 사용한다.  

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

### findNameById 메서드 구현(명명된 파라미터)
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
파라미터는 Map객체를 이용하여 전달한다.  

### insert 메서드 구현
#### SqlUpdate이용한 자료 수정
```java
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
```
SqlUpdate클래스를 상속받아 InsertSinger클래스를 생성한다.  
InsertSinger클래스의 생성자에서 데이타소스와 sql문을 설정하고, 
이름이 부여된 파라미터를 매핑하기 위하여 declareParameter메서드에 
SqlParamter클래스 객체로 파라미터명과 타입을 선언한다.     
setGeneratedKeysColumnNames메서드를 이용하여 자동으로 생성되는 키칼럼을 설정한다.    

SQL문 처리후 자동으로 생성된 ID값은 KeyHolder를 이용하여 받아온다.  

### insertWithAlbum 메서드 구현
#### BatchSqlUpdate를 이용한 배치작업
```java
    @Override
    public void insertWithAlbum(Singer singer) {
        InsertAlbum insertAlbum = new InsertAlbum(dataSource);
        insert(singer);
        List<Album> albums = singer.getAlbums();
        if(albums != null) {
            for(Album album:albums) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("singer_id", singer.getId());
                paramMap.put("title",album.getTitle());
                paramMap.put("release_date", album.getReleaseDate());
                insertAlbum.updateByNamedParam(paramMap);
            }
            insertAlbum.flush();
        }
    }

    private static class InsertAlbum extends BatchSqlUpdate{
        private static final String sql = "insert into album (singer_id, title, release_date)\n"+
                "values(:singer_id, :title, :release_date)";
        private static final int BATCH_SIZE = 10;
        
        public InsertAlbum(DataSource dataSource) {
            super(dataSource, sql);
            declareParameter(new SqlParameter("singer_id", Types.INTEGER));
            declareParameter(new SqlParameter("title", Types.VARCHAR));
            declareParameter(new SqlParameter("release_date", Types.DATE));
            setBatchSize(BATCH_SIZE);
        }
    }
```
BatchSqlUpdate.setBatchSize()를 이용하여 배치처리 횟수를 지정한다.  
SqlUpdate는 바로바로 실행되지만 BatchSqlUpdate는 배치 사이즈 회수만큼 발생할 때 마다 한꺼번에 처리한다.  
BatchSqlUpdate.flush()는 배치사이즈에 도달하지 않아 기다리는 sql문을 처리한다.

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.

## 정리
선언적 트랜잭션, 명명된 파라미터등을 지원한다.  
Spring에서 제공하는 JdbcTemplate을 사용하면 Connection, Statement를 close할 필요가 없다.  
스프링에서 알아서 처리해 준다. 또한 선언적 트랜잭션 관리도 가능하다.  

 
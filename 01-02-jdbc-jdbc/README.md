# Native JDBC를 이용한 구현
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
PreparedStatement를 이용하여 "select * from singer"sql문을 실행한 후  
결과를 Singer클래스 객체에 담은 리스트객체를 리턴하는 메서드를 구현하였다.  
@Override어노테이션은 인터페이스로 상속받은 메서드임을 표시한다.  
@Transactional(readOnly=true)을 표기하여 트랜잭션을 읽기전용으로 설정하였다.  
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
클래스 단에서 이미 @Transactional어노테이션을 선언하였기 때문에 트랜잰션 선언을 하지 않더라도 트랜잭션을 탄다.

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.
```log
INFO  SingerDaoTests - Starting SingerDaoTests on linor with PID 8454 (started by linor in /home/linor/git/linor-space/01-02-jdbc-jdbc)
INFO  SingerDaoTests - No active profile set, falling back to default profiles: default
INFO  HikariDataSource - HikariPool-1 - Starting...
INFO  HikariDataSource - HikariPool-1 - Start completed.
INFO  SingerDaoTests - Started SingerDaoTests in 2.028 seconds (JVM running for 3.305)
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@157853da, testMethod = testSingerUpdate@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoTests - >>> 김종서 수정 전 >>>
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1970-12-09, albums=null)
INFO  SingerDaoTests - >>> 김종서 수정 후 >>>
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@157853da, testMethod = testSingerUpdate@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@642413d4, testMethod = testFindNameById@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@642413d4, testMethod = testFindNameById@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@fb2e3fd, testMethod = testFindAll@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoImpl - 가수명 : 김건모, 생년월일: 1999-07-12
INFO  SingerDaoImpl - 가수명 : 조용필, 생년월일: 1978-06-28
INFO  SingerDaoImpl - 가수명 : 태진아, 생년월일: 2000-11-01
INFO  SingerDaoImpl - 가수명 : 김종서, 생년월일: 1977-10-16
INFO  SingerDaoTests - 가수목록
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=null)
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=null)
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=null)
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  SingerDaoImpl - 가수명 : 김건모, 생년월일: 1999-07-12
INFO  SingerDaoImpl - 가수명 : 조용필, 생년월일: 1978-06-28
INFO  SingerDaoImpl - 가수명 : 태진아, 생년월일: 2000-11-01
INFO  SingerDaoImpl - 가수명 : 김종서, 생년월일: 1977-10-16
INFO  SingerDaoImpl - 가수명 : 홍길동, 생년월일: 1991-01-11
INFO  SingerDaoTests - 가수 추가 후 가수 목록
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=null)
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=null)
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=null)
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  SingerDaoTests - Singer(id=5, firstName=길동, lastName=홍, birthDate=1991-01-11, albums=null)
INFO  SingerDaoImpl - 가수명 : 김건모, 생년월일: 1999-07-12
INFO  SingerDaoImpl - 가수명 : 조용필, 생년월일: 1978-06-28
INFO  SingerDaoImpl - 가수명 : 태진아, 생년월일: 2000-11-01
INFO  SingerDaoImpl - 가수명 : 김종서, 생년월일: 1977-10-16
INFO  SingerDaoTests - 가수 삭제 후 가수 목록
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=null)
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=null)
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=null)
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@fb2e3fd, testMethod = testFindAll@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@3f183caa, testMethod = testFindAllWidthAlbums@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=[Album(id=1, singerId=1, title=아름다운 구속, releaseDate=2019-01-01), Album(id=2, singerId=1, title=날개를 활짝펴고, releaseDate=2019-02-01)])
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=[Album(id=3, singerId=2, title=황혼의 문턱, releaseDate=2019-03-01)])
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=[])
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=[])
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@3f183caa, testMethod = testFindAllWidthAlbums@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@24534cb0, testMethod = testFindbyId@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoTests - 주키로 1개 레코드 검색 결과>>>
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@24534cb0, testMethod = testFindbyId@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@5a50d9fc, testMethod = testFindByFirstName@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@5a50d9fc, testMethod = testFindByFirstName@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@2274160, testMethod = contextLoads@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@2274160, testMethod = contextLoads@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@65383667, testMethod = testInsertSingerWithAlbum@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=[Album(id=1, singerId=1, title=아름다운 구속, releaseDate=2019-01-01), Album(id=2, singerId=1, title=날개를 활짝펴고, releaseDate=2019-02-01)])
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=[Album(id=3, singerId=2, title=황혼의 문턱, releaseDate=2019-03-01)])
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=[])
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=[])
INFO  SingerDaoTests - Singer(id=6, firstName=태원, lastName=김, birthDate=1965-04-12, albums=[Album(id=4, singerId=6, title=Never Ending Story, releaseDate=2001-08-31), Album(id=5, singerId=6, title=생각이나, releaseDate=2009-08-14), Album(id=6, singerId=6, title=사랑할수록, releaseDate=1993-11-01)])
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@65383667, testMethod = testInsertSingerWithAlbum@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  TransactionContext - Began transaction (1) for test context [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@7a3e5cd3, testMethod = testInsertSinger@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]; transaction manager [org.springframework.jdbc.datasource.DataSourceTransactionManager@2cec704c]; rollback [true]
INFO  SingerDaoImpl - 가수명 : 김건모, 생년월일: 1999-07-12
INFO  SingerDaoImpl - 가수명 : 조용필, 생년월일: 1978-06-28
INFO  SingerDaoImpl - 가수명 : 태진아, 생년월일: 2000-11-01
INFO  SingerDaoImpl - 가수명 : 김종서, 생년월일: 1977-10-16
INFO  SingerDaoImpl - 가수명 : 김태원, 생년월일: 1965-04-12
INFO  SingerDaoImpl - 가수명 : 김조한, 생년월일: 1990-10-16
INFO  SingerDaoTests - >>> 김조한 추가후
INFO  SingerDaoTests - Singer(id=2, firstName=건모, lastName=김, birthDate=1999-07-12, albums=null)
INFO  SingerDaoTests - Singer(id=3, firstName=용필, lastName=조, birthDate=1978-06-28, albums=null)
INFO  SingerDaoTests - Singer(id=4, firstName=진아, lastName=태, birthDate=2000-11-01, albums=null)
INFO  SingerDaoTests - Singer(id=1, firstName=종서, lastName=김, birthDate=1977-10-16, albums=null)
INFO  SingerDaoTests - Singer(id=6, firstName=태원, lastName=김, birthDate=1965-04-12, albums=null)
INFO  SingerDaoTests - Singer(id=7, firstName=조한, lastName=김, birthDate=1990-10-16, albums=null)
INFO  TransactionContext - Rolled back transaction for test: [DefaultTestContext@2b4bac49 testClass = SingerDaoTests, testInstance = com.linor.singer.SingerDaoTests@7a3e5cd3, testMethod = testInsertSinger@SingerDaoTests, testException = [null], mergedContextConfiguration = [MergedContextConfiguration@fd07cbb testClass = SingerDaoTests, locations = '{}', classes = '{class com.linor.singer.JdbcApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@401e7803, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@704d6e83, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2f0a87b3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@15b3e5b], contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map[[empty]]]
INFO  HikariDataSource - HikariPool-1 - Shutdown initiated...
INFO  HikariDataSource - HikariPool-1 - Shutdown completed.
```


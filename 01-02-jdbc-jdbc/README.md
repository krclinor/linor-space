# Native JDBC를 이용한 구현
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 일반적인 JDBC프로그램으로 구현한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

## 의존성 라이브러리
todo 프로젝트와 동일하게 설정한다.  
소스 : [pom.xml](pom.xml)

## 설정
### 데이타 소스 설정
todo 프로젝트와 동일하게 설정한다.  
소스 : [application.yml](src/main/resources/application.yml)  

### 데이타베이스 초기화 파일 생성
todo 프로젝트와 동일하게 설정한다.  

소스 : [schema.sql](src/main/resources/schema.sql)  
소스 : [data.sql](src/main/resources/data.sql)  

## Domain 클래스 생성
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  

## DAO인터페이스 생성
todo 프로젝트와 동일하게 구현.  
소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  

## SingerDao인터페이스 구현
SinerDao인터페이스에서 선언한 모든 메서드를 구현한다.  
소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/jdbc/SingerDaoImpl.java)  

### 클래스에 선언한 어노테이션
```java
@Slf4j
@Repository
@RequiredArgsConstructor
public class SingerDaoImpl implements SingerDao {
```
@Slf4j는 로그를 사용하기 위해 선언한다.  
@Repository는 스프링어노테이션으로 데이타베이스 저장소를 구현하기 위해 설정한다.  
@RequiredArgsConstructor를 선언하여 final맴버변수를 생성자 파라미터로 사용하도록 한다.


### 데이타소스 선언 
```java
    private final DataSource dataSource;
``` 
@RequiredArgsConstructor를 선언하였기 때문에 lombok가 다음 생성자를 만들어 낸다.  
```java
	public SingerDaoImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
```
생성자의 파라미터로 등록된 데이타소스는 스프링이 데이타소스를 주입 한다.  
주입하는 데이타소스는 application.yml에서 설정한 datasource이다.  
  
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
				Singer singer = Singer.builder()
						.id(rs.getInt("id"))
						.firstName(rs.getString("first_name"))
						.lastName(rs.getString("last_name"))
						.birthDate(rs.getDate("birth_date").toLocalDate())
						.build();
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
@Override어노테이션은 인터페이스에서 상속받은 메서드임을 표시한다.  
@Slf4j어노테이션을 설정하였기 때문에 log.info나 log.error메서드를 사용하여 로그를 뿌릴 수 있다.  

Native JDBC를 이용하여 구현할 때 적용되는 전형적이 방식으로 데이타소스에서 Connection을 가져와서, 
Connection.prepareStatement()로 SQL문을 파라미터값으로 전달하여 Statement를 생성한 다음 
PreparedStatement.executeQuery()를 실행하여 결과를 ResultSet으로 받아와서 
결과가 배열인 경우 ResultSet.next()가 참인동안 
각각의 결과를 ResultSet.getString(), ResultSet.getInt()등을 이용하여 객체에 담는다.  
조회가 완료되면 사용했던 ResultSet, PreparedStatement, Connection을 모두 닫는다.  

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
insert메서드는 가수 객체를 데이타베이스에 추가하는 작업을 수행한다.  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.  
![](./images/image01.png)  
Junit으로 테스트케이스를 실행하면 위와같은 화면으로 각 메서드별 실행결과를 확인할 수 있다.  

## 정리
가장 다루기 힘든 처리방법이며, 스프링에서 제공하는 선언적 트랜잭션을 사용할 수 없다.  
사용한 Resultset, Statement, Connection은 메서드 종료전에 모두 닫아야 한다. 그렇지 않으면 메모리 누수가 발생할 수 있다.

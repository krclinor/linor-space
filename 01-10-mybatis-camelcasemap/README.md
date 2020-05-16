# CamelCase맵 구현 및 Mybatis에서 사용하기
CamelCase로 만든 도메인 객체를 생성하여 사용하는 경우 간단한 설정으로 SQL select문에 있는 SnakeCase칼럼을 CamelCase객체 맴버변수에 알아서 넣어준다.  
Mybatis를 사용할 경우 다양한 많은 select문을 만들게 된다. 이때마다 해당 도메인 객체를 만들면 너무 많은 객체가 만들어 진다.  
도메인 객체를 줄이는 방법으로 Map을 사용하면 되지만 Map을 사용하는 경우 Mybatis가 SnakeCase를 CamelCase로 변환해 주지 않는다.  
HashMap을 상속하여 CamelCase맵을 만들어서 사용해 본다.  

기존 mybatis프로젝트를 이용하여 구현한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
mybatis 프로젝트와 동일.    
소스 : [pom.xml](pom.xml)

## 설정
mybatis 프로젝트와 동일.    
소스 : [application.yml](src/main/resources/application.yml)  

### 데이타베이스 초기화 파일 생성
mybatis 프로젝트와 동일.  
소스 : [schema.sql](src/main/resources/schema.sql)  
소스 : [data.sql](src/main/resources/data.sql)  

## Domain 클래스 생성
### CamelCaseMap 생성
소스 : [CamelCaseMap.java](src/main/java/com/linor/singer/domain/CamelCaseMap.java)
```java
public class CamelCaseMap extends HashMap<String, Object>{
	
	private String toProperCase(String source, boolean isCapital) {
		String result = "";
		if(isCapital) {
			result = source.substring(0,1).toUpperCase() + source.substring(1).toLowerCase();
		}else {
			result = source.toLowerCase();
		}
		return result;
	}
	
	private String toCamelCase(String source) {
		String[] parts = source.split("_");
		StringBuilder camelCaseString = new StringBuilder();
		for(int i = 0; i < parts.length; i++){
			String part = parts[i];
			camelCaseString.append(toProperCase(part, (i != 0 ? true: false)));
		}
		return camelCaseString.toString();
	}

	@Override
	public Object put(String key, Object value) {
		return super.put(toCamelCase(key), value);
	}
	
	public Object cput(String key, Object value) {
		return super.put(key, value);
	}
}
```
Map.put()호출시 키를 CamelCase로 변환하도록 메서드를 오버라이딩(Overriding)한다.   
기존 키값이 CamelCase인 경우 put()메서드를 호출하면 안되므로 기존의 put메서드를 대신할 cput()메서드를 만들어 놓는다.  

Singer클래스를 제외한 모든 클래스가 CamelCaseMap클래스를 이용해서 처리할 수 있어 Album, Instrument, SingerSummary 클래스는 제거한다.  

소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Singer {
	private Integer id;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	
	private Set<CamelCaseMap> albums;
	private Set<CamelCaseMap> instruments;
}
```
Singer 클래스 내에 맴버변수로 있던 Album과 Instrument클래스도 CamelCaseMap으로 대체한다.  

## DAO인터페이스 생성
소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  
```java
@Mapper
public interface SingerDao {
	List<CamelCaseMap> findAll();
	List<CamelCaseMap> findAllByNativeQuery();

	List<Singer> findByFirstName(String firstName);
	List<Singer> findByFirstNameAndLastName(CamelCaseMap singer);
	List<Singer> findAllWithAlbums();

	List<CamelCaseMap> findAlbumsBySinger(CamelCaseMap singer);
	List<CamelCaseMap> findAlbumsByTitle(String title);

	String findNameById(Integer id);
	String findFirstNameById(Integer id);

	CamelCaseMap findById(Integer id);
	void insert(CamelCaseMap singer);
	void update(CamelCaseMap singer);
	void delete(Integer singerId);
	
	void insertWithAlbum(CamelCaseMap singer);

	void insertInstrument(CamelCaseMap instrument);

	public List<CamelCaseMap> listAllSingersSummary();
}
```
클래스 내의 맴버변수에 배열객체를 만들어서 리턴해야 하는 findAllWithAlbums, findByFirstNameAndLastName, findByFirstName메서드를 제외하고는 모두 CamelCaseMap으로 대체가능하다.  

## SingerDao인터페이스 구현
소스 : [SingerDao.xml](src/main/resources/com/linor/singer/dao/SingerDao.xml)  


### findAllCamelCaseMap 메서드 구현
```xml
<select id="findAll" resultType="CamelCaseMap">
	select * from singer
</select>
```
기존 Dao인터페이스에서 리턴 타입이 CamelCaseMap이거나 List&#60;CamelCaseMap&#62;인 경우 resultType을 CamelCaseMap으로 수정한다.  

## 결과 테스트
소스 : [SingerDaoTests.java](src/test/java/com/linor/singer/SingerDaoTests.java)
```java
	@Test
	public void testFindAll(){
		log.info("testFindAll---->>");
		List<CamelCaseMap> singers = singerDao.findAll();
		assertNotNull(singers);
		assertTrue(singers.size() == 4);
		log.info("가수목록");
		singers.forEach(singer -> {
			log.info(singer.toString());
		});
	}
```
Dao인터페이스에서 리턴 타입이 CamelCaseMap이거나 List&#60;CamelCaseMap&#62;인 변수들을 모두 리턴 타입에 맞게 수정한다.  
Junit으로 SingerDaoTests를 실행한다.  

## 정리
CamelCaseMap을 만들어 사용하면 상당히 많은 도메인객체를 줄일 수 있다.  
하지만 sql문을 보지 않으면 도메인 객체에서 쉽게 확인할 수 있었던 맴버변수를 알아내기 어렵다.

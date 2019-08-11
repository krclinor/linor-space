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
mybatis 프로젝트와 동일.  
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  

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
}
```
Map.put()호출시 키를 CamelCase로 변환하도록 메서드를 오버라이딩(Overriding)한다.   

## DAO인터페이스 생성
소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  
```java
@Mapper
public interface SingerDao {
    List<CamelCaseMap> findAllCamelCaseMap();
    List<Singer> findAll();
    List<Singer> findByFirstName(String firstName);
    String findNameById(Integer id);
    Singer findById(Integer id);
    String findFirstNameById(Integer id);
    void insert(Singer singer);
    void update(Singer singer);
    void delete(Integer singerId);
    List<Singer> findAllWithAlbums();
    void insertWithAlbum(Singer singer);
}
```
findAllCamelCaseMap()은 findAll()과 동일한 기능을 하며  List<Singer>에서 List<CamelCaseMap>으로 변경하여 구현한다.

## SingerDao인터페이스 구현
소스 : [SingerDao.xml](src/main/resources/com/linor/singer/dao/SingerDao.xml)  


### findAllCamelCaseMap 메서드 구현
```xml
<select id="findAllCamelCaseMap" resultType="CamelCaseMap">
    select * from singer
</select>
```
기존 findAll을 복사하여 id는 findAllCamelCaseMap, resultType은 CamelCaseMap으로 수정한다.  

## 결과 테스트
Junit으로 SingerDaoTests를 실행한다.
테스트 케이스이 findAllCamelCaseMap()을 시험하도록 다음을 추가한다.

소스 : [SingerDaoTests.java](src/test/java/com/linor/singer/SingerDaoTests.java)
```java
    @Test
    public void testFindAllCamelCaseMap(){
        List<CamelCaseMap> singers = singerDao.findAllCamelCaseMap();
        assertNotNull(singers);
        assertTrue(singers.size() == 4);
        log.info("가수목록(CamelCase)");
        singers.forEach(map ->{
            log.info(map.toString());
        });
    }
```

## 정리
CamelCaseMap을 만들어 사용하면 상당히 많은 도메인객체를 줄일 수 있다.  
하지만 sql문을 보지 않으면 도메인 객체에서 쉽게 확인할 수 있는 맴버변수를 알아내기 어렵다.

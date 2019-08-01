# Mybatis를 이용한 구현
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 Mybatis로 구현한다.  
## Mybatis 설정
application.yml파일에 Mybatis를 설정한다.
```yml
#마이바티스
mybatis:
  mapper-locations: classpath*:/**/dao/*.xml
  type-aliases-package: com.linor.singer.domain
  configuration.map-underscore-to-camel-case: true
```
mapper-locations는 sql문을 처리하는 mybatis mapper파일의 위치를 지정한다.
type-aliases-package를 등록하면 도메인사용시 패키지명을 사용하지 않고도 도메인을 지정할 수 있다.  
예) com.linor.singer.domain.Album -> Album  
configuratioins.map-underscore-to-camel-case를 true로 설정하면 테이블 컬럼의 snake case를 camel case로 변환하여 
ORM매핑처리를 한다.  

## 인터페이스에 mapper어노테이션 추가
```java
@Mapper
public interface SingerDao {
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
dao인터페이스에 @Mapper어노테이션을 추가하여 인터페이스를 Mybatis Mapper에서 구현하도록 한다.

## SingerDao인터페이스 구현
Mybatis Mapper인터페이스 구현은 인터페이스 내에 어노테이션으로 처리할 수도 있고 별도 XML파일로 처리할 수도 있다.  
여기에서는 XML로 처리하는 방법을 설명한다.  

### Mapper 선언부
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.linor.singer.dao.SingerDao">
```
처음 2줄은 무조건 추가한다.  
namespace에 구현할 인터페이스 명을 등록한다.

## 정리
Mybatis는 전자정부프레임워크에서 Persistence레이어를 담당하고 있어 중요하고 좋은 도구이다.  
SQL문을 잘 다루는 개발자에게 적합하고, 모든 SQL문을 별도의 저장공간에서 관리할 수 있어 편리하다.  
단점은 SQL문을 개발자가 직접 구현해야 하며, 데이터베이스가 바뀔 경우 SQL문을 수정해 주어야 한다.

 
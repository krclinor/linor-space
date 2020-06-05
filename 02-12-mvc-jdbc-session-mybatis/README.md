# Mybatis를 활용한 Spring Boot 보안개발
이 프로젝트에서는 세션정보를 데이타베이스에서 관리하는 방법을 배운다.  
이 프로젝트는 mvc-security-mybatis를 복사하여 진행한다.  

## 설정
### 메이븐 의존성 추가
소스 : [pom.xml](pom.xml)  
```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-jdbc</artifactId>
</dependency>
```
프로젝트의 pom.xml에 spring-session-jdbc를 추가한다.  

### 세션 저장타입 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
spring:
  session.store-type: jdbc
```
application.yml에서 세션 저장타입을 jdbc로 선언한다  

### 스프링 세션 설정 클래스 생성
소스: [JdbcSessionConfig.java](src/main/java/com/linor/singer/config/JdbcSessionConfig.java)  
```java
@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 60)
public class JdbcSessionConfig {
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource datasource) {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("org/springframework/session/jdbc/schema-drop-postgresql.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("org/springframework/session/jdbc/schema-postgresql.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("data.sql"));

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(datasource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }
}
```
@EnableJdbcHttpSession을 선언하고 maxInactiveIntervalInSeconds파라미터에 60을 설정하여 60초단위로 종료된 세션을 정리하도록 한다.  
dataSourceInitializer 빈을 생성하여 데이타베이스 벤더에 맞는 테이블 삭제 및 생성 sql파일과, 스키마 및 데이타 적재 sql파일을 등록한다.  
세션관련 테이블생성 sql파일은 spring-session-jdbc-2.3.0.RELEASE.jar 파일에 존재한다.  

## 간단한 어플 구현
### 컨트롤러 구현
소스 : [SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)  
```java
...
	@GetMapping("/colors")
	public String index(Model model, HttpSession session) {
		List<String> favoriteColors = getFavoritColors(session);
		model.addAttribute("favoriteColors", favoriteColors);
		model.addAttribute("sessionId", session.getId());
		return "colors";
	}
	private List<String> getFavoritColors(HttpSession session){
		List<String> favoritColors = (List<String>)session.getAttribute("favoriteColors");
		if(favoritColors == null) {
			favoritColors = new ArrayList<>();
		}
		return favoritColors;
	}
	@RequestMapping("/saveColor")
	public String saveMessage(@ModelAttribute("color") String color, HttpSession session) {
		List<String> favoritColors = getFavoritColors(session);
		if(!StringUtils.isEmpty(color)) {
			favoritColors.add(color);
			session.setAttribute("favoriteColors", favoritColors);
		}
		return "redirect:/colors";
	}
	@GetMapping("/closeSession")
	public String closeSession(HttpSession session) {
		session.invalidate();
		return "redirect:/colors";
	}
}
```
세션관련 메서드
- session.setAttribute(): 세션에 값을 저장
- session.getAttribute(): 세션에서 값을 추출  
- session.invalidate(): 세션 종료로 로그아웃과 동일한 효과가 발생

### Jsp View 구현
소스 : [colors.jsp](src/main/webapp/WEB-INF/jsp/colors.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>환영합니다!!</title>
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/4.5.0/css/bootstrap.min.css"/>
</head>
<body>
	<div class="alert alert-primary" role="alert">
		좋아하는 새깔 목록<br>
		세션ID : ${sessionId}
	</div>
	<table class="table">
	  <thead>
	    <tr>
      		<th scope="col">색깔</th>
      	</tr>
     </thead>
      <tbody>
      	<c:forEach var="color" items="${favoriteColors}">
      	<tr>
      		<td>
      			${color}
      		</td>
      	</tr>
      	</c:forEach>
      </tbody>
	</table>	
	<form:form action="/saveColor" method="post">
	  <div class="form-group">
	    <label for="color">좋아하는 색깔</label>
	    <input type="text" class="form-control" name="color" placeholder="색깔 입력">
	  </div>
	  <button type="submit" class="btn btn-primary">세션에 저장</button>
	  <a class="btn btn-secondary" href="<c:url value="/closeSession"/>">세션 종료</a>
	  
	</form:form>
	<script type="text/javascript" src="webjar/bootstrap/4.5.0/js/bootstrap.min.js"></script>
</body>
</html>
```
프로젝트에 crsf를 적용한 경우에 폼처리는 스프링에서 제공하는 &lt;form:form&gt;태그를 사용해야 한다. 그렇지 않으면 다음과 같은 오류가 화면에 나타난다.  
```text
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Fri Jun 05 11:33:17 KST 2020
There was an unexpected error (type=Method Not Allowed, status=405).
Request method 'POST' not supported
```
 
## 결과 테스트
브라우저에서 다음 주소를 호출하여 테스트 해 본다.  
http://localhost:8080/colors  

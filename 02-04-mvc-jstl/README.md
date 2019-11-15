# Spring Boot에서 JSTL 적용하기

## Spring Boot Starter를 이용한 프로젝트 생성
Spring boot Starter로 프로젝트 생성시 패키징은 war로 설정한다.
```xml
	<packaging>war</packaging>
```

### 의존성 라이브러리
Spring initializer로 생성시 기본 dependency는 Web, DevTools, Lombok를 선택한다.
프로젝트 생성 후 pom.xml에 tomcat-jasper, jstl, bootstrap을 추가한다.
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- JSTL for JSP -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>
		<!-- Need this to compile JSP -->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
			<scope>provided</scope>
		</dependency>
		<!-- Optional for static content. bootstrap CSS -->
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>bootstrap</artifactId>
			<version>4.3.1</version>
		</dependency>
	</dependencies>
```
## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp

welcome.message: 안녕하세요 스프링부트 JSP예제 입니다. 
```
사용포트는 설정하지 않으면 디폴트가 8080포트를 사용한다.  
jsp파일의 위치를 설정하기 위해 sprint.mvc.view.prefix와 suffix를 설정한다.
welcom.message는 컨트롤러에서 사용하기 위해 설정한다.  

### 메인 프로그램 수정
소스 :[Application.java](src/main/java/com/linor/singer/Application.java)
```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```
Tomcat과 같은 WAS에 war파일로 디플로이 할 수 있도록 하기 위해 메인 Application클래스를 SpringBootServletInitializer로 상속받아 위와 같이 구현한다.  

### 컨트롤러 생성
소스 :[SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)  

```java
@Controller
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	@RequestMapping("/")
	public String welcom(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
}
```
yml설정파일에 있는 welcom.message값을 담을 message 인스턴스 변수를 생성한다.
루트 URL호출시 welcom메서드가 실행되도록 @RequestMapping("/")를 지정한다.
welcom메서드에서 "welcome"를 리턴하면 스프링은 yml설정의 prefix와 suffix를 조합하여 /WEB-INF/jsp/welcome.jsp파일을 호출한다.  

### 뷰 JSP(
소스 [welcome.jsp](src/main/webapp/WEB-INF/jsp/welcom.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>환영합니다!!</title>
	<link rel="stylesheet" type="text/css" href="/webjars/bootstrap/4.3.1/css/bootstrap.min.css"/>
	<c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet"/> 
	<script src="/webjars/jquery/3.0.0/jquery.min.js"></script>
	<script src="/webjars/popper.js/1.14.3/umd/popper.min.js"></script>
	<script src="/webjars/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<a class="navbar-brand" href="#">스프링 부트</a>
		<button class="navbar-toggler" type="button" 
				data-toggle="collapse" 
				data-target="#navbarSupportedContent" 
				aria-controls="navbarSupportedContent" 
				aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active">
					<a class="nav-link" href="#">홈 <span class="sr-only">(current)</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="#">링크</a>
				</li>
			</ul>
		</div>
	</nav>
	<div class="container">
		<div class="starter-template">
			<h1>Spring Boot 웹 JSP 예제</h1>
			<h2>메시지 : ${message}</h2>
		</div>
	</div>
</body>
</html>
```
JSTL태그라이브러리를 사용하기 위해 다음과 같이 선언한다.
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080
 
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
			<version>4.5.0</version>
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
welcome.message는 컨트롤러에서 사용하기 위해 설정한다.  

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
	public String welcome(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
}
```
yml설정파일에 있는 welcome.message값을 담을 message 인스턴스 변수를 생성한다.  
루트 URL호출시 welcome메서드가 실행되도록 @RequestMapping("/")를 지정한다.  
welcome메서드에서 "welcome"를 리턴하면 스프링은 yml설정의 prefix와 suffix를 조합하여 /WEB-INF/jsp/welcome.jsp파일을 호출한다.  

### 뷰 JSP
소스 [welcome.jsp](src/main/webapp/WEB-INF/jsp/welcome.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta charset="UTF-8">
<title>환영합니다!!</title>
	<link rel="stylesheet" type="text/css" href="/webjars/bootstrap/4.5.0/css/bootstrap.min.css"/>
	<c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet"/> 
	<script src="/webjars/jquery/3.5.1/jquery.min.js"></script>
	<script src="/webjars/popper.js/1.16.0/umd/popper.min.js"></script>
	<script src="/webjars/bootstrap/4.5.0/js/bootstrap.min.js"></script>
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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```
c태그라이브러리는 jstl에서 가장 많이 사용하는 태그라이브러리 이다.
#### forEach를 사용한 특정 범위의 숫자값 목록 표시  
```jsp
<table>
<tr>
  <th>Value</th>
  <th>Square</th>
</tr>
<c:forEach var="x" begin="0" end="10" step="2">
<tr>
  <td><c:out value="${x}"/></td>
  <td><c:out value="${x * x}"/></td>
</tr>
</c:forEach>
</table>
```

#### forEach를 이용한 Collection형 배열 처리  
forEach는 Collection, Map, Iterator, Enumeration, Array(Object/Primitive), 쉼표로 구분된 String, SQL쿼리 결과값 (javax.servlet.jsp.jstl.sql.Result) 등의 배열객체를 처리한다.
```jsp
<table>
  <c:forEach items="${entryList}" var="blogEntry">
    <tr><td align="left" class="blogTitle">
      <c:out value="${status.count}"/>.
      <c:out value="${blogEntry.title}" escapeXml="false"/>
    </td></tr>
    <tr><td align="left" class="blogText">
      <c:out value="${blogEntry.text}" escapeXml="false"/>
    </td></tr>
  </c:forEach>
</table>
```
위의 모드는 블로그 글목록을 출력하는 예시이다.  
${entryList}는 title, text를 멤버 변수로 갖는 특정 객체의 집합 배열객체이다.  
현재 순환중인 객체가 blogEntry에 저장되며 .을 사용하여 title과 text를 출력한다.  

 ${status.count}에서는 현재 몇번째 순환중인지 값을 확인할 수 있다.  
사용가능한 변수의 종류는 다음과 같다.  
- current : 현재 순환중인 아이템
- index : 현재 순환중인 아이템의 인덱스(0베이스)
- count : 현재 순환중인 아이템의 인덱스(1베이스)
- first : 현재 순환중인 아이템이 첫번째 아이템인지 여부를 확인(Boolean)
- last : 현재 순환중인 아이템이 마지막 아이템인지 여부를 확인 (Boolean)
- begin : forEach에서 지정할 수 있는 begin값
- end : forEach에서 지정할 수 있는 end값
- step : forEach에서 지정할 수 있는 step값

#### if를 사용한 조건문
다음의 코드는 첫번째 아이템이 순환중일 경우 블로그글이 언제 작성되었는지 날짜를 출력하도록 수정된 코드이다.  
test안에 Boolean형이 반환될 수 있는 어떤 수식을 사용해도 된다.  
```jsp
    <c:if test="${status.first}">
      <tr><td align="left" class="blogDate">
            <c:out value="${blogEntry.created}"/>
      </td></tr>
    </c:if>
```

#### choose를 사용한 다중 조건문 활용
```jsp
<c:choose>
  <c:when test="${pageContext.request.scheme eq 'http'}">
    This is an insecure Web session.
  </c:when>
  <c:when test="${pageContext.request.scheme eq 'https'}">
    This is a secure Web session.
  </c:when>
  <c:otherwise>
    You are using an unrecognized Web protocol. How did this happen?!
  </c:otherwise>
</c:choose>
```

#### url을 사용하여 주소 새성
이 태그는 현재의 서블릿 컨텍스트 이름을 자동으로 앞에 붙여주고 세션관리와 파라미터의 이름과 값의 인코딩을 자동으로 지원한다.  
기본적인 사용법은 다음과 같다.  
```jsp
<a href="<c:url value='/content/sitemap.jsp'/>">View sitemap</a>
```

<c:param>을 사용하여 파라미터를 추가할 수 있다.
```jsp
<c:url value="/content/search.jsp">
  <c:param name="keyword" value="${searchTerm}"/>
  <c:param name="month" value="02/2003"/>
</c:url>
```

#### import를 사용하여 페이지 첨부하기
JSP에는 기본적으로 두가지 방법의 페이지 안에 다른 컨텐츠를 추가하는 방법이 존재한다.  
include지시자와 <jsp:include> 액션이 있지만, 둘 모두 같은 웹 어플리케이션 또는 서블릿 컨텍스트 안에있는 페이지만을 불어들일 수 있다.  
core라이브러리에 있는 <c:import> 액션은 좀더 일반적이고 강력한 기능을 가진다.  
사용 문법은 <c:url>과 매우 배슷하며 심지어 <c:param>도 그대로 사용할 수 있다.  
```jsp
<c:import url="ftp://ftp.example.com/package/README"/>
```
<c:import>에는 var와 scope 두가지 필수적이지 않은 속성이 존재
- var : 불러들인 페이지를 곧바로 출력하지 않고 String형 변수로 담아두기 위해 사용 
- scope : 변수의 스코프를 지정할 수 있다. 디폴트는 page

#### catch로 예외처리 하기
import는 ftp에도 접속이 가능하다.  
다음 예시는 만약에 해당 위치에 파일이 존재하지 않거나 네트워크의 문제로 페이지를 불러올 수 없는 상황이라면 예외가 발생할 것이다.  
예외가 발생할 경우 var에 예외가 저장된다.  
<c:if>를 통해 예외가 발생했는지 확인한다.
```jsp
<c:catch var="exception">
  <c:import url="ftp://ftp.example.com/package/README"/>
</c:catch>
<c:if test="${not empty exception}">
  Sorry, the remote content is not currently available.
</c:if>
```

#### redirect를 이용한 페이지 리다이렉트
&#60;jsp:forward&#62; 액션과도 매우 흡사하다. 하지만 이 기능의 경우에는 서버사이드에서 구현된 요청형태만을 포워딩 한다.  
포워딩의 경우에는 사용자 입장에서 보면 페이지의 이동 없이 다른 페이지를 띄워줄 수 있지만 리다이렉트의 경우에는 브라우저에 의해 페이지의 이동이 일어나게 된다.  
하지만 &#60;c:redirect&#62;액션이 좀 더 유연하다.  
&#60;jsp:forward&#62;의 경우에는 현재 같은 서블릿 컨텍스트 내의 다른 페이지로만 이동 할 수 있기 때문이다.

원본 사이트 : http://theeye.pe.kr/archives/1563

## 결과 테스트트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080
 
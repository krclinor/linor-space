# Spring Boot에서 JSTL Form 처리
웹개발시 사용자로부터 자료를 입력받아 시스템에서 처리하도록 한다.
이때 필요한 것이 폼이다.  
사용자의 성, 이름, 이메일, 비밀번호, 생일, 성별, 국가, 흡연여부 등을 입력하여 처리하는 웹화면을 구현해 보자.  

## Spring Boot Starter를 이용한 프로젝트 생성
프로젝트 생성은 mvc-jstl프로젝트와 동일한 방식으로 생성한다.  

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
```
사용포트는 설정하지 않으면 디폴트가 8080포트를 사용한다.  
jsp파일의 위치를 설정하기 위해 sprint.mvc.view.prefix와 suffix를 설정한다.

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

### 폼데이터 관리를 위한 모델 클래스 생성
소스: [User.java](src/main/java/com/linor/singer/model/User.java)   

```java
@Data
public class User {
	private String name;
	private String lastName;
	private String email;
	private String password;
	private String detail;
	
	@DateTimeFormat(pattern="yyyy.MM.dd")
	private LocalDate birthDate;
	
	private Gender gender;
	private String country;
	private boolean nonSmoking;
	
	@NumberFormat(pattern="#,##0")
	private long salary;
}
```
@Data는 클래스 인스턴스 변수인 name, lastName등의 get/set메서드를 자동으로 생성해주는 lombok어노테이션이다.  
User클래스에 name, lastName, email, password, detail, birthDate, gender, country,noSmoking, salary 프로퍼티를 등록한다.
birthDate에 선언한 @DateTimeFormat(pattern="yyyy.MM.dd")은 웹화면에서 날짜등록 포멧을 yyyy.MM.dd형식으로 입력받도록 하고 이러한 형식의 문자열을 LocalDate타입으로 자동변환해 준다.  
salary에 선언한 @NumberFormat(pattern="#,##0")은 웹화면에서 금액 입력시 천단위 콤마를 표시하는 숫자를 입력받을 수 있도록 한다.

### Enum 사용
다양한 경험을 위해 성별을 Enum으로 정의해 보자.  
소스: [Gender.java](src/main/java/com/linor/singer/model/Gender.java)  
```java
public enum Gender{
	MALE("남"),
	FEMALE("여");
	
	private String value;
	
	Gender(String value){
		this.value = value;
	}
	
	public String getKey() {
		return name();
	}
	
	public String getValue() {
		return value;
	}
}
```

### 컨트롤러 생성
소스 :[UserController.java](src/main/java/com/linor/singer/controller/UserController.java)  

```java
@Controller
public class UserController {
	private static final String[] countries =
		{"대한민국", "터어키", "미국", "일본"};
	
	@RequestMapping(value="/form")
	public String user(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("genders", Gender.values());
		model.addAttribute("countries", countries);
		return "userForm";
	}
	
	@RequestMapping(value = "/result")
	public String processUser(User user, Model model) {
		model.addAttribute("u", user);
		return "userResult";
	}
}
```
/form을 브라우저에서 호출하면 user()메서드가 처리된다.

```java
		model.addAttribute("user", new User());
```
모델에 user객체를 생성하여 user속성을 설정한다.

```java
		model.addAttribute("genders", Gender.values());
		model.addAttribute("countries", countries);
```
콤보박스에서 표시하기 위한 성별목록을 전달하기 위해 Enum.values()로 Enum타입을 배열로 변경하여 genders속성을 설정한다.  

```java
		return "userForm";
```

#### Model, ModelMap, ModelAndView 차이
model.addAttribute()와 modelmap.addAttribute() 함수를 사용하여 Model에 데이터를 저장 후 view에서 데이터에 접근이 가능하다.  
차이점으로는 Model은 인터페이스이며 ModelMap은 구현체이다.  
Spring에서 내부적으로 사용하는 객체의 타입은 동일하며 사용의 구분은 개발자의 취향  

ModelAndView는 Model과 View를 동시에 설정이 가능하며 컨트롤러는 ModelAndView객체를 리턴
```java
	@RequestMapping(value = "/result")
	public ModelAndView processUser(User user) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("userResult");
		modelAndView.addObject("u", user);
		return modelAndView;
	}
```

### 뷰 JSP 
JSP뷰 파일인 /WEB-INF/jsp/userForm.jsp를 호출한다.  
소스 [userForm.jsp](src/main/webapp/WEB-INF/jsp/userForm.jsp)
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>MVC 폼처리 예제</title>
</head>
<body>
<h2>사용자 등록 폼</h2>
<form:form modelAttribute="user" action="result">
	<table>
		<tr>
			<td><form:label path="lastName">성</form:label></td>
			<td><form:input path="lastName"/></td>
		</tr>
		<tr>
			<td><form:label path="name">이름</form:label></td>
			<td><form:input path="name"/></td>
		</tr>
		<tr>
			<td><form:label path="email">이메일</form:label></td>
			<td><form:input path="email"/></td>
		</tr>
		<tr>
			<td><form:label path="password">비밀번호</form:label></td>
			<td><form:password path="password"/></td>
		</tr>
		<tr>
			<td><form:label path="detail">소개</form:label></td>
			<td><form:textarea path="detail"/></td>
		</tr>
		<tr>
			<td><form:label path="birthDate">생일</form:label></td>
			<td><form:input path="birthDate"/></td>
		</tr>
		<tr>
			<td><form:label path="gender">성별</form:label></td>
			<td>
				<form:radiobuttons path="gender" items="${genders}" itemValue="key" itemLabel="value"/>
			</td>
		</tr>
		<tr>
			<td><form:label path="country">국가</form:label></td>
			<td><form:select path="country" items="${countries}"/></td>
		</tr>
		<tr>
			<td><form:label path="nonSmoking">금연여부</form:label></td>
			<td><form:checkbox path="nonSmoking"/></td>
		</tr>
		<tr>
			<td><form:label path="salary">월급여</form:label></td>
			<td><form:input path="salary"/></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" value="전송"/></td>
		</tr>
	</table>
</form:form>	
</body>
</html>
```

```jsp
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
```
스프링에서 제공하는 form태그라이브러리를 사용하기 위해 선언한다.   

브라우저로 전송되는 HTML은 다음과 같다.
```html
<html><head>
<meta charset="UTF-8">
<title>MVC 폼처리 예제</title>
</head>
<body>
<h2>사용자 등록 폼</h2>
<form id="user" action="result" method="post">
	<table>
		<tbody><tr>
			<td><label for="lastName">성</label></td>
			<td><input id="lastName" name="lastName" type="text" value=""></td>
		</tr>
		<tr>
			<td><label for="name">이름</label></td>
			<td><input id="name" name="name" type="text" value=""></td>
		</tr>
		<tr>
			<td><label for="email">이메일</label></td>
			<td><input id="email" name="email" type="text" value=""></td>
		</tr>
		<tr>
			<td><label for="password">비밀번호</label></td>
			<td><input id="password" name="password" type="password" value=""></td>
		</tr>
		<tr>
			<td><label for="detail">소개</label></td>
			<td><textarea id="detail" name="detail"></textarea></td>
		</tr>
		<tr>
			<td><label for="birthDate">생일</label></td>
			<td><input id="birthDate" name="birthDate" type="text" value=""></td>
		</tr>
		<tr>
			<td><label for="gender">성별</label></td>
			<td>  
				<span>
					<input id="gender1" name="gender" type="radio" value="MALE">
					<label for="gender1">남</label>
				</span>
				<span>
					<input id="gender2" name="gender" type="radio" value="FEMALE">
					<label for="gender2">여</label>
				</span>
			 </td>
		</tr>
		<tr>
			<td><label for="country">국가</label></td>
			<td>
				<select id="country" name="country">
					<option value="대한민국">대한민국</option>
					<option value="터어키">터어키</option>
					<option value="미국">미국</option>
					<option value="일본">일본</option>
				</select>
			</td>
		</tr>
		<tr>
			<td><label for="nonSmoking">금연여부</label></td>
			<td>
				<input id="nonSmoking1" name="nonSmoking" type="checkbox" value="true">
				<input type="hidden" name="_nonSmoking" value="on">
			</td>
		</tr>
		<tr>
			<td><label for="salary">월급여</label></td>
			<td><input id="salary" name="salary" type="text" value="0"></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" value="전송">	</td>
		</tr></tbody>
	</table>
</form>	

</body></html>
```

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080
 
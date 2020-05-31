# Spring Boot 메서드 보안
메서드별로 보안을 설정한다.    
이 프로젝트는 mvc-security-mybatis를 복사하여 진행한다.  

## 보안 설정 수정
기존 WebSecurityConfig클래스에 @EnableGlobalMethodSecurity를 추가한다.   
소스 : [WebSecurityConfig.java](src/main/java/com/linor/singer/config/WebSecurityConfig.java)
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
```
@EnableGlobalMethodSecurity을 이용하여 메서드레벨에서 사용할 보안을 설정한다.  
- prePostEnabled : true로 설정하면 @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter을 사용할 수 있다.
- securedEnabled : true로 설정하면 @Secured을 사용할 수 있다.
- jsr250Enabled : true로 설정하면 @RolesAllowed, @PermitAll, @DenyAll을 사용할 수 있다.

## 서비스 인터페이스 및 구현 클래스 생성
먼저 서비스 인터페이스를 만들고 인터페이스에 해당하는 구현 인터페이스를 만든다.  
소스 : [HelloService.java](src/main/java/com/linor/singer/service/HelloService.java)  
```java
public interface HelloService {
	public String getHelloMessage();
}
```

소스 : [HelloServiceImpl.java](src/main/java/com/linor/singer/serviceImpl/HelloServiceImpl.java)
```java
@Service
public class HelloServiceImpl implements HelloService {
	@Override
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String getHelloMessage() {
		return "안녕하세요!!";
	}
}
```
구현클래스의 메서드에 @PreAuthorize어노테이션을 이용하여 ADMIN권한을 체크하도록 설정한다.  
어노테이션에 내장 액세스 표현식 뿐 아니라 메서드의 파라미터 조합을 이용한 권한체크도 가능하다.  
```java
@PreAuthorize("#contact.name == authentication.name")
public void doSomething(Contact contact){
```

## 컨트롤러에 서비스 호출 컨트롤 메서드 추가
컨트롤러에서 서비스를 호출하는 메서드와 URL을 추가한다.  
소스 : [SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)  
```java
	@RequestMapping("hello")
	public String sayHello(Model model) {
		model.addAttribute("message", hellService.getHelloMessage());
		return "hello";
	}
```
helloService에서 만든 메시지를 모델에 담아서 hello.jsp로 넘긴다.  

[hello.jsp](src/main/webapp/WEB-INF/jsp/hello.jsp)

## 결과 테스트
브라우저에서 다음 주소를 호출하여 테스트 해 본다.  
http://localhost:8080/  

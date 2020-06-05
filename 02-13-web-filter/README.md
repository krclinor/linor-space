# Filter 사용
목적 : 스프링 부트에서 필터 사용법을 습득한다.  
  
## Spring Boot Starter를 이용한 프로젝트 생성
Spring boot Starter로 프로젝트 생성시 패키징은 war로 설정한다.
```xml
	<packaging>war</packaging>
```

### 의존성 라이브러리
Spring initializer로 생성시 기본 dependency는 Web, DevTools, Lombok를 선택한다.  
소스 : [pom.xml](pom.xml)  
```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-devtools</artifactId>
		<scope>runtime</scope>
	</dependency>
	<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
	</dependency>
</dependencies>
```

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
logging:
  pattern:
    console: '[%p] [%t] %c{0}.%M[%L] - %m%n'
%msg%n'
  level:
    root: info
```
테스트 결과를 로그로 확인하기 위하여 로그를 설정한다.  

## Filter 생성
### 전역 로그 Filter
모든 url에 대한 로그를 남기는 필터를 만든다.  
소스 : [RequestLogFilter.java](src/main/java/com/linor/app/component/RequestLogFilter.java)  
```java
@Component
@Order(1)
@Slf4j
public class RequestLogFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		log.info("요청 로그: {} - {} ", req.getMethod(), req.getRequestURI());

		chain.doFilter(request, response);

		log.info("응답 로그: {}", response.getContentType());
	}

}
```
@Component를 선언하여 이 Filter가 빈임을 선언한다.  
전역 Filter가 여러개 존재하는 경우 처리 순서를 정하기 위해 @Order를 사용하여 순서를 설정한다.  
RequestLogFilter는  요청 처리전에 요청에 대한 method와 URL을 로그로 출력하고 요청 처리 후에 응답에 대한 ContentType을 로그로 출력한다.  

### Url패턴 로그 Filter
특정 문자열을 포함하는 URL만 필터링하는 Filter를 만든다.  
소스 : [RequestLogRegistFilter.java](src/main/java/com/linor/app/component/RequestLogRegistFilter.java)  
```java
@Slf4j
public class RequestLogRegistFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		long startMilis = System.currentTimeMillis();
		chain.doFilter(request, response);
		log.info("처리시간 : {} 밀리초", System.currentTimeMillis() - startMilis );
	}
}
```
이 필터는 요청을 처리하는데 걸리는 시간을 밀리초단위로 계산하여 로그에 기록한다.  

특정 URL만 필터링하기 위해서는 설정빈이 필요하다.  
소스 : [ControllersConfig.java](src/main/java/com/linor/app/config/ControllersConfig.java)  
```java
@Configuration
public class ControllersConfig  implements WebMvcConfigurer{
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/hello");
	}
	@Bean
	public FilterRegistrationBean<RequestLogRegistFilter> loggingFilter(){
		FilterRegistrationBean<RequestLogRegistFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLogRegistFilter());
		registrationBean.addUrlPatterns("/user/*");
		return registrationBean;
	}
}
```
FilterRegistrationBean을 리턴하는 빈을 생성한다.  
addUrlPatterns()를 이용하여 특정 문자열을 포함하는 URL패턴을 등록한다.  
registrationBean.addUrlPatterns("/user/*");은 /user/로 시작하는 URL에 대하여 필터링하도록 한다.  

## 테스트용 컨트롤러 생성
소스: [HelloController.java](src/main/java/com/linor/app/controller/HelloController.java)  
```java
@RestController
public class HelloController {
	@GetMapping("/hello")
	public String sayHello1() {
		return "Hello World!";
	}
	@GetMapping("/user/hello")
	public String sayHello2() {
		return "Hello User!!";
	}
}
```
리턴문자열이 뷰를 호출하지 않고 직접 화면에 보이도록 하기 위해 @RestController를 선언한다.  
전역 필터만 처리되는 "/hello" URL 처리 메서드와 전역 필터와 특정URL 필터가 모두 처리되는 "/user/hello" URL 처리 메서드를 생성하였다.  

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080/hello  
http://localhost:8080/user/hello

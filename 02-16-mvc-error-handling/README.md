# Spring Boot MVC에서 에러 처리하기
Spring Boot MVC에서 Exception을 처리하는 방법은 보통 3가지 정도가 있다.
-  WebMvcConfigurer인터페이스를 상속받아 ExceptionResolver를 구현
-  Controller에 ExceptionHandler 생성
-  Exception을 처리하는 ControllerAdvice 클래스 생성

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 20-04-mvc-jstl을 복사하여 02-16-mvc-error-handling프로젝트를 생성한다.

## 1. SimpleMappingExceptionsResolver를 이용한 처리
소스 : [WebMvcConfig.java](src/main/java/com/linor/singer/config/WebMvcConfig.java)
```java
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		mappings.setProperty("DataAccessException", "dbError");
		mappings.setProperty("RuntimeException", "error");
		
		exceptionResolver.setExceptionMappings(mappings);
		exceptionResolver.setDefaultErrorView("error");
		return exceptionResolver;
	}
}
```
SimpleMappingExceptionResolver빈을 등록하여 에러 처리하는 방법이다.  
DataAccessException이 발생하면 dbError.jsp를 호출하고, RuntimeException이 발생하면 error.jsp페이지를 호출하도록 한다.  

## 2. Controller에서 @ExceptionHandler를 이용한 처리
먼저 ResourceNotFoundException이라는 예외를 생성한다.  
소스 : [ResourceNotFoundException.java](src/main/java/com/linor/singer/exception/ResourceNotFoundException.java)
```java
public class ResourceNotFoundException extends RuntimeException {
	
	public ResourceNotFoundException() {
		this("해당 자원이 존재하지 않습니다.");
	}
	public ResourceNotFoundException(String message){
		this(message, null);
	}
	
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
```

컨트롤러에서 @ExceptionHandler를 생성하고 이를 이용한 Exception을 발생하는 로직을 추가한다.
소스 : [SingerController.java](src/main/java/com/linor/singer/exception/SingerController.java)
```java
@Controller
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	@RequestMapping("/")
	public String welcom(Map<String, Object> model) {
		
		if(message == null)
			throw new ResourceNotFoundException();
		
		model.put("message", this.message);
		return "welcome";
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ModelAndView handleResourceNotFoundException(ResourceNotFoundException e) {
		ModelAndView model = new ModelAndView("error/404");
		model.addObject("exception", e);
		return model;
	}
}
```
handlerResourceNotFoundException에서 에러가 발생하면 error/404.jsp를 호출하도록 한다.  
 
## 3. ControllerAdvice 클래스 생성
Exception을 처리할 ControllerAdvice클래스를 생성한다.
소스 : [GlobalExceptionHandler.java](src/main/java/com/linor/singer/advice/GlobalExceptionHandler.java)
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BizException.class)
	public String handleBizException(HttpServletRequest req, BizException bizEx) {
		log.info("BizException Occurred:: URL=" + req.getRequestURL());
		return "biz_error";
	}
	
	@ExceptionHandler(ServletRequestBindingException.class)
	public String servletRequestBindingException(ServletRequestBindingException ex) {
		log.error("ServletRequestBindingException occurred: " + ex.getMessage());
		return "validation_error";
	}
}
```
BizException발생시 biz_error.jsp를 호출하고,
ServletRequestBindingException발생시 validation_error.jsp를 호출한다.  
 
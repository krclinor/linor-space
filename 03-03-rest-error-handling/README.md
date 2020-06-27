# Spring Boot REST 에러 처리
Spring Boot Rest에서 Exception을 Exception에서 @ResponseStatus를 이용하여 처리하는 방법과 ControllerAdvice를 이용하여 처리하는 방법을 소개한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 rest-mybatis를 복사하여 프로젝트를 생성한다.  

## 작업 준비
### Exception 클래스 생성
소스 : [BizException.java](src/main/java/com/linor/singer/exception/BizException.java)
```java
public class BizException extends RuntimeException {
	
	public BizException() {
		this("알수 없는 오류가 발생했습니다.");
	}
	public BizException(String message){
		this(message, null);
	}
	
	public BizException(String message, Throwable cause) {
		super(message, cause);
	}
}
```
BizException은 비즈니스오류 처리용으로 사용한다.  

소스 : [DataAccessException.java](src/main/java/com/linor/singer/exception/DataAccessException.java)
```java
public class DataAccessException extends SQLException {
	
	public DataAccessException() {
		this("알수 없는 오류가 발생했습니다.");
	}
	public DataAccessException(String message){
		this(message, null);
	}
	
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
```
DataAccessException은 원하는 데이타베이스 처리시 발생하는 오류를 처리하는데 사용하도록 한다.  

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
ResourceNotFoundException은 원하는 자원이 존재하지 않을 경우 발행하도록 한다.  

### REST컨트롤러 생성
위에서 생성한 Exception을 발생하도록 REST 컨트롤러를 생성한다.  
소스 : [SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)
```java
@RestController
@RequestMapping("/rest/singer")
public class SingerController {
	@Autowired
	private SingerDao singerDao;
	
	@GetMapping
	public List<Singer> getSingers(){
		return singerDao.findAllWithAlbums();
	}
	
	@GetMapping(value = "/{id}")
	public Singer getSinger(@PathVariable("id") int id) {
		Singer singer = singerDao.findById(id);
		if(singer == null)
			throw new ResourceNotFoundException();
		
		return singer;
	}
	
	@PostMapping
	public void addSinger(@RequestBody Singer singer) {
		if(null != singer.getId())
			throw new BizException("비즈니스 오류 발생");
		singerDao.insertWithAlbum(singer);
	}
	
	@PutMapping(value="/{id}")
	public void updateSinger(@PathVariable("id") int id, @RequestBody Singer singer) {
		singerDao.update(singer);
	}
	
	@DeleteMapping(value = "/{id}")
	public void deleteSinger(@PathVariable("id") int id) throws Exception{
		Singer singer = singerDao.findById(id);
		if (singer == null)
			throw new DataAccessException("데이타가 존재하지 않음");
		singerDao.delete(id);
	}
}
```
- REST용 컨트롤러로 사용하기 위해 @RestController를 선언한다.
- @RequestMapping("/rest/singer")를 선언하여 모든 메서드의 패스가 /rest/singer로 시작하도록 한다.  

## 1. @ResponseStatus를 이용한 처리
Exception클래스에 @ResponseStatus로 HTTP상태코드를 선언하여 처리한다.  
소스 : [BizException.java](src/main/java/com/linor/singer/exception/BizException.java)
```java
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class BizException extends RuntimeException {
...
}
```
BizException이 발생하면 HTTP상태코드를 SERVICE_UNAVAILABLE(503)으로 리턴하도록 한다.  

소스 : [DataAccessException.java](src/main/java/com/linor/singer/exception/DataAccessException.java)
```java
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataAccessException extends SQLException {
...
}
```
DataAccessException은 HTTP상태코드를 INTERNAL_SERVER_ERROR(500)으로 리턴하도록 한다.  

소스 : [ResourceNotFoundException.java](src/main/java/com/linor/singer/exception/ResourceNotFoundException.java)
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
...
}
```
ResourceNotFoundException은 HTTP상태코드를 NOT_FOUND(404)으로 리턴하도록 한다.  

### 테스트
postman프로그램을 이용하여 데이타베이스에 없는 가수id로 호출한다.  
http://localhost:8080/rest/singer/6  
```json
{
    "timestamp": "2020-06-27T02:23:39.343+00:00",
    "status": 404,
    "error": "Not Found",
    "trace": "com.linor.singer.exception.ResourceNotFoundException: 해당 자원이 존재하지 않습니다.\n\tat com.linor.singer.controller.SingerController.getSinger(SingerController.java:37)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:564)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:
    ...
    ",
    "message": "해당 자원이 존재하지 않습니다.",
    "path": "/rest/singer/6"
}
```
결과 JSON형식으로 timestamp, status, error, trace, message, path정보를 받을 수 있다.  

## 2. ControllerAdvice를 이용한 처리
스프링부트에서 자동으로 제공하는 정보가 아닌 사용자정의(Custom)정보를 제공하기 위해 ExceptionHandler를 사용한다.  
먼저 사용자정의 정보를 담기 위해 도메인을 생성한다.  

소스 : [ErrorDetail.java](src/main/java/com/linor/singer/domain/ErrorDetail.java)
```java
@Data
public class ErrorDetail {
	private int errorCode;
	private String errorMessage;
	private String devErrorMessage;
}
```
에러코드, 에러메시지, 개발자가 보기 위한 trace정보를 제공한다.  

컨트롤러에 ExceptionHandler를 처리하는 메서드로 구현하면 컨트롤러 내에서만 에러처리를 한다.  
시스템 전체적으로 사용할 수 있도록 하기 위해 ControllerAdvice클래스를 생성한다.  
소스 : [ControllExceptionHandler.java](src/main/java/com/linor/singer/advice/ControllExceptionHandler.java)
```java
@ControllerAdvice
public class ControllExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BizException.class)
	public ResponseEntity<?> bizException(BizException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<?> dataAccessException(DataAccessException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String getStackTraceAsString(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}
```

### 테스트
postman프로그램을 이용하여 데이타베이스에 없는 가수id로 호출한다.  
http://localhost:8080/rest/singer/6  
```json
{
    "errorCode": 404,
    "errorMessage": "해당 자원이 존재하지 않습니다.",
    "devErrorMessage": "com.linor.singer.exception.ResourceNotFoundException: 해당 자원이 존재하지 않습니다.\n\tat com.linor.singer.controller.SingerController.getSinger(SingerController.java:37)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n
    ...
    "
}
```
결과 JSON형식으로 사용자정의 에러메시지 errorCode, errorMessage, devErrorMessage정보를 받을 수 있다.  


 
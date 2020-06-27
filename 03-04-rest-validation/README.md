# Spring Boot REST 유효성 처리
Spring Boot Rest에서 ControllerAdvice를 이용하여 유효성 처리하는 방법을 소개한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 rest-error-handling을 복사하여 프로젝트를 생성한다.  

## 작업 준비
### pom.xml에 validation의존성 추가
소스 : [pom.xml](pom.xml)
```xml
	...
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
	</dependencies>
```
의존성에 유횽체크 라이브러리인 spring-boot-starter-validation을 추가한다.  

### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
spring:
  messages:
    basename: messages
    cache-duration: -1
    encoding: UTF-8
    fallback-to-system-locale: true
```
spring.messages는 메시지 Properties파일을 설정하기 위해 사용한다.  
-  basename : Properties파일명을 설정한다. messages.properties파일로 설정하기 위해 messages로 입력함.
-  cache-duraton : -1로 입력하면 시작시 메모리에 올려서 서버가 죽을 때까지 사용한다.

### 메시지 프로퍼티파일 설정
설정파일에서 설정한 메시지 내용은 다음과 같다.  
소스 : [messages.properties](src/main/resources/messages.properties)

```properties
error.name=이름은 {min}에서 {max}개의 문자열로 작성합니다.
error.lastName=성은 필수입력입니다.
```
시스템에서 사용할 메시지를 등록한다.

#### Validation 메시지 소스 설정
소스 : [WebConfig.java](src/main/java/com/linor/singer/config/WebConfig.java)  
```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
	private final MessageSource messageSource;
	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource);
		return factory;
	}
}
```
Validation에서 사용할 메시지 소스를 지정하기 위해 WebMvcConfigurer를 상속받아 WebConfig설정 클래스를 생성한다.    
메시지 소스는 applicaition.yml에서 설정한 messages.properties파일이 설정된다.  

## 도메인 클래스에 유효성 체크 어노테이션 추가
가수클래스에 유효성체크 어노테이션을 추가한다.  
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Singer {
	private Integer id;

	@Size(min = 2, max = 10, message = "{error.name}")
	private String firstName;

	@NotEmpty(message = "{error.lastName}")
	private String lastName;

	@Past
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;
	
	private Set<Album> albums;
	private Set<Instrument> instruments;
}
```
유효성 체크를 위해 Validation어노테이션들을 추가한다.  
-  @Size(min = 2, max = 10, message = "{error.name}") : 최소 2문자, 최해 10문자로 제한하고 체크에서 벗어 나는 경우 messsages.properties파일의 error.name메시지를 메시지로 제공한다.  
-  @NotEmpty(message = "{error.lastName}") : 필수 입력 설정으로 값이 없을 경우 error.lastName메시지를 제공한다.
-  @Email(message = "{error.email}") : 데이타가 이메일 패턴에 맞는지 체크한다.
-  @Pattern(regexp = "^[a-zA-Z]\\w{3,14}$") : 정규 표현식에 맞는지 체크
-  @Past : 날짜관련 타입이 과거인지 체크
-  @Min(value = 100) : 최소값이 설정된 값 이상인지 체크

## 유효성 체크용 도메인 생성
항목별 오류 메시지를 담을 FieldErrorMessage클래스를 생성한다.  
소스 : [FieldErrorMessage.java](src/main/java/com/linor/singer/domain/FieldErrorMessage.java)
```java
@Data
@Builder
public class FieldErrorMessage {
	private String resource;
	private String field;
	private String code;
	private String message;
}
```
- resource: 객체명
- field: 객체의 맴버변수
- code: 오류코드
- message: 오류메시지

ExceptionHandler에서 오류정보를 담는 ErrorDetail클래스에 필드오류 메시지를 추가한다.  
소스 : [ErrorDetail.java](src/main/java/com/linor/singer/domain/ErrorDetail.java)
```java
@Data
public class ErrorDetail {
	private int errorCode;
	private String errorMessage;
	private String devErrorMessage;

	private List<FieldErrorMessage> fieldErrors;
}
```
## 유효성 체크를 위한 Exception 생성
소스 : [ValidException.java](src/main/java/com/linor/singer/exception/ValidException.java)
```java
@Getter
public class ValidException extends RuntimeException{
	private Errors errors;
	
	public ValidException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}
}
```
오류메시지와 오류객체를 받아서 생성하는 생성자를 추가한다.  

## ControllerAdvice에 ValidationException 추가
ControllerAdvice클래스에 유효성체크 Exception을 추가한다.  
소스 : [ControllExceptionHandler.java](src/main/java/com/linor/singer/advice/ControllExceptionHandler.java)
```java
@ControllerAdvice
public class ControllExceptionHandler {
...
	@ExceptionHandler(ValidException.class)
	public ResponseEntity<?> validException(ValidException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		errorDetail.setErrorMessage(e.getMessage());

		List<FieldErrorMessage> fieldErrorMessages = new ArrayList<>();
		e.getErrors().getFieldErrors().forEach(fieldError -> {
			FieldErrorMessage fieldErrorMessage = FieldErrorMessage.builder()
					.resource(fieldError.getObjectName())
					.field(fieldError.getField())
					.code(fieldError.getCode())
					.message(fieldError.getDefaultMessage())
					.build();
			fieldErrorMessages.add(fieldErrorMessage);
		});
		errorDetail.setFieldErrors(fieldErrorMessages);

		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		
		return new ResponseEntity<>(errorDetail, HttpStatus.UNPROCESSABLE_ENTITY);
	}
...
}
```

## 컨트롤러에 유효성 체크 로직 추가

소스 : [SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)
```java
@RestController
@RequestMapping("/rest/singer")
public class SingerController {
	...	
	@PostMapping
	public void addSinger(@Valid @RequestBody Singer singer, BindingResult result) {
		if(result.hasErrors()) {
			throw new ValidException("유효성 체크 오류", result);
		}
		if(null != singer.getId())
			throw new BizException("비즈니스 오류 발생");
		singerDao.insertWithAlbum(singer);
	}
	
	@PutMapping(value="/{id}")
	public void updateSinger(@PathVariable("id") int id, @Valid @RequestBody Singer singer,
			BindingResult result) {
		if(result.hasErrors()) {
			throw new ValidException("유효성 체크 오류", result);
		}
		singerDao.update(singer);
	}
	...	
}
```
유효성체크는 주로 데이타를 추가,수정하는 메서드에서 처리한다.  
객체 앞에 @Valid를 선언하고, 메서드에 BindingResult파라미터를 추가한다.  
result에 오류가 존재하는 경우 ValidException을 발생시킨다.    

### 테스트
postman프로그램을 이용하여 오류정보가 있는 가수를 추가한다.    
- url: http://localhost:8080/rest/singer
- method: post 
- 데이타: 
```json
{"firstName":"기", "lastName":"", "birthDate":"2020-12-09"}
```

- 결과 :
```json
{
    "errorCode": 422,
    "errorMessage": "유효성 체크 오류",
    "devErrorMessage": "com.linor.singer.exception.ValidException: 유효성 체크 오류\n\tat com.linor.singer.controller.SingerController.addSinger(SingerController.java:49)\n
    ...",
    "fieldErrors": [
        {
            "resource": "singer",
            "field": "birthDate",
            "code": "Past",
            "message": "과거 날짜여야 합니다"
        },
        {
            "resource": "singer",
            "field": "firstName",
            "code": "Size",
            "message": "이름은 2에서 10개의 문자열로 작성합니다."
        },
        {
            "resource": "singer",
            "field": "lastName",
            "code": "NotEmpty",
            "message": "성은 필수입력입니다."
        }
    ]
}
```

 
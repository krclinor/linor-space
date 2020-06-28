# Spring Boot CORS
- CORS(Cross-Origin Resource Sharing, 교차 출처 리소스 공유): 추가 HTTP 헤더를 사용하여, 한 출처에서 실행 중인 웹 애플리케이션이 다른 출처의 선택한 자원에 접근할 수 있는 권한을 부여하도록 브라우저에 알려주는 체제  

보안 상의 이유로, 브라우저는 스크립트에서 시작한 교차 출처 HTTP 요청을 제한한다. 이를 SOP(Same-Origin Policy, 동일 출처 정책)이라고 한다.  
예를 들어, XMLHttpRequest와 Fetch API는 동일 출처 정책을 따른다.  
즉, 이 API를 사용하는 웹 애플리케이션은 자신의 출처와 동일한 리소스만 불러올 수 있으며, 다른 출처의 리소스를 불러오려면 그 출처에서 올바른 CORS 헤더를 포함한 응답을 반환해야 한다.

REST API를 제공하는 백엔드 서버와, HTML및 자바스크립트등을 제공하는 프론트엔드 서버가 분리되어 있는 경우 자바스크립트로 REST API를 호출하면 값을 받아올 수 없다.  
이를 방지하기 위해 프론트엔트 서버인 경우 CORS를 설정한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 rest-mybatis를 복사하여 프로젝트를 생성한다.  

## 테스트용 html파일 생성
소스 : [test.html](src/main/resources/public/test.html)
```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>REST테스트</title>
<script src="https://code.jquery.com/jquery-3.5.1.min.js" integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=" crossorigin="anonymous"></script>
<script>
$(document).ready(function(){
	$("#singer").click(function(){
		$.getJSON("http://localhost:8080/rest/singer", function(data, status){
			var strData = JSON.stringify(data);
			alert(strData);
		});
	});
});
</script>
</head>
<body>
	<button id="singer">가수목록 가져오기</button>
</body>
</html>
```
JQuery를 이용하여 백엔드 서버에서 http://localhost:8080/rest/singer를 호출하여 가수 목록을 가져와서 팝업창으로 화면에 결과를 보여준다.  

스프링부트를 시작한다.
```sh
./mvnw spring-boog:run
```

## 테스트
다음은 src/main/resources/public디렉토리에서 Visual Studio Code를 실행한다.  
```sh
src/main/resources/public$ code .
```
test.html파일을 열어서 Visual Studio Code의 확장팩인 Live Server를 실행한다.  
![](images/image01.png) 

스프링부트는 8080포트로 서비스하고, Live Server는 보통 5500포트로 서비스하여 2개의 URL로 테스트한다.  
- 테스트URL1: http://localhost:8080/test.html
- 테스트URL2: http://localhost:5500/test.html

### 8080포트 테스트
테스트URL1: http://localhost:8080/test.html
![](images/image02.png)  
백엔드와 프론트엔드 서버의 출처가 동일하기 때문에 가수목록 가져오기 버튼을 클릭하면 결과를 제대로 보여준다. 

### 5500포트 테스트
테스트URL2: http://localhost:5500/test.html
![](images/image03.png)  
5500포트에서 가수목록 가져오기 버튼을 클릭하면 화면에 아무런 반응이 없다.  

크롬의 개발자도구를 열어 Console탭으로 이동하면 다음과 같은 오류를 확인할 수 있다.  
![](images/image04.png)  

## 1. 컨트롤러 메서드에 어노테이션 설정으로 처리하는 방법
소스 : [SingerController.java](src/main/java/com/linor/singer/controller/SingerController.java)
```java
@RestController
@RequestMapping("/rest/singer")
public class SingerController {
	@Autowired
	private SingerDao singerDao;
	
	@GetMapping
	@CrossOrigin("*")
	public List<Singer> getSingers(){
		return singerDao.findAllWithAlbums();
	}
	...
```
컨트롤러의 메서드에 @CrossOrigin을 선언하여 CORS를 허용할 수 있다.  
출처를 여러개 설정하려면 다음과 같이 설정할 수 있다.
```java
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"}) 
```

모든 출처를 허용하려면 다음과 같이 *를 설정한다.  
```java
@CrossOrigin("*"}) 
```
@CrossOrigin를 Class레벨에 선언하면 Class내의 모든 메서드에 적용된다.  


![](images/image05.png)  
결과 5500포트에서도 호출이 가능하게 되었다.  

## 2. WebMvcConfigurer 설정으로 처리하는 방법
WebMvcConfigurer설정을 통하여 CORS를 설정하면 많은 API를 한번에 설정할 수 있다.  
소스 : [CorsConfig.java](src/main/java/com/linor/singer/config/CorsConfig.java)  

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/rest/**")
			.allowedOrigins("http://localhost:5500")
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowCredentials(false)
			.maxAge(3600);
	}
}
```

## 출처
- https://developer.mozilla.org/ko/docs/Web/HTTP/CORS

 
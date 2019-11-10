# Spring Boot의 정적 페이지 위치
스프링 부트에서 사용하는 정적페이지의 홈디렉토리의 위치는 JSP를 사용하지 않는 경우 보통
-  /src/main/resources/public와 
-  /src/main/resources/static이다.

## Spring Boot Starter를 이용한 프로젝트 생성
### 의존성 라이브러리
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
			<groupId>org.webjars.bower</groupId>
			<artifactId>bootstrap</artifactId>
			<version>4.3.1</version>
		</dependency>
	</dependencies>
```
sprinb Boot에서 웹을 사용하기 위해서는 spring-boot-starter-web을 설치한다.  
bootstrap은 다음 프로젝트에서 설명할 webjar 패키지이다.

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
server:
  port: 8080
  servlet:
    context-path: /
```
사용할 서버의 포트를 8080으로 설정하였다.  
컨텍스트 패스는 /로 설정하였다.

## 정적 페이지 구현
### html페이지
보통 html페이지는 /src/main/resources/public에 구현한다.  
소스 : [index.hmtl](src/main/resources/public/index.html)  
```html
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>홈화면</title>
	<link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.css"/>
	<link rel="sytlesheet" href="/css/styles.css"/>	
</head>
<body>
	<nav class="navbar fixed-top navbar-expand-lg navbar-dark bg-dark">
		<a href="#" class="navbar-brand">프로젝트 명</a>
		<button class="navbar-toggler" type="button"
			data-toggle="collapse" data-target="#navbarSupport"
			aria-controls="navbarSupportedContent"
			aria-expanded="false"
			aria-label="네비게이션 토글">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div id="navbarSupportedContent" class="collapse navbar-collapse">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active">
					<a href="#" class="nav-link">홈</a>
				</li>
				<li class="nav-item">
					<a href="#about" class="nav-link">소개</a>
				</li>
				<li class="nav-item">
					<a href="#contact" class="nav-link">연락처</a>
				</li>
			</ul>
		</div>
	</nav>
	<div class="container mt-2">
		<h2>환영합니다!!</h2>
		<img src="/images/spring-boot.jpeg" alt="스프링 부트">
	</div>
</body>
</html>
```
위 파일의 URL은 http://localhost:8080/ 또는 http://localhost:8080/index.html이다.
 
### Cascading StyleSheet 구현
보통 css는 /src/main/resources/static/css에 구현한다.  
소스 : [style.css](src/main/resources/static/css/style.css)  
```css
@charset "UTF-8";
body {
	background-color: #a7a5a4;
	padding-top: 50px;
}
```
위에 만든 css파일의 URL은 http://localhost:8080/css/style.css가 된다.

### 이미지 파일 위치
보통 이미지 파일은 /src/main/resources/static/images 하위폴더에 위치시킨다.

## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
https://localhost:8080
 
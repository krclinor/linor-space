# Actuator
Spring Boot Actuator는 어플리케이션을 모니터링하고 제어하기 위해 사용한다.

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  
spring-boot-starter-web, spring-boot-starter-actuator를 추가한다.

### 의존성 라이브러리
소스 : [pom.xml](pom.xml)
```xml   
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
	</dependencies>
```   
### application.yml에 actuator 설정사항 추가
소스 : [applicaition.yml](src/main/resources/applicaition.yml)
```yml
management:
  endpoints:
    web:
#      base-path: /app #디폴트 /actuator
      exposure:
        include: '*'
```
management.endpoints.web.exposure.include에 *를 설정함으로써 모든 자원을 볼 수 있도록 한다.
노출을 제외하려면 management.endpoints.web.exposure.exclude에 추가한다.
디폴트 패스가 /actuator로 변경하려면 base-path를 설정하면 된다.  
 
### 테스트1
프로젝트를 구동하고, 브라우저에서 다음 url을 연다.  
http://localhost:8080/actuator
![image01](images/image01.png)
결과 조회화면

## 보안 추가
### 의존성 라이브러리
소스 : [pom.xml](pom.xml)
```xml   
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```   
의존성 라이브러리에 spring-boot-starter-security를 추가한다.

### application.yml에 보안 설정사항 추가
소스 : [applicaition.yml](src/main/resources/applicaition.yml)
```yml
spring:
  security:
    user:
      name: admin
      password: secret
      roles:
      - ADMIN
```
디폴트 사용자와 사용자의 비밀번호를 설정한다. 
권한은 ADMIN으로 한다.  

### 테스트2
프로젝트를 다시 구동하고, 브라우저에서 다음 url을 연다.  
http://localhost:8080/actuator
![image02](images/image02.png)
결과 사용자와 비밀번호를 입력하도록 한다.  
![image01](images/image01.png)
결과 

## 어플리케이션의 정보 추가
### application.yml에 내용을 추가
소스 : [applicaition.yml](src/main/resources/applicaition.yml)
```yml
info:
  app:
    name: Beginning Spring Boot 2
    description: This is a SpringBoot Demo app
    version: 1.0.0
```
### 테스트3
http://localhost:8080/actuator/info
![image03](images/image03.png)
결과 








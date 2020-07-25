# FTP Channel Adapter
ftp서버의 /test1/디렉토리에 존재하는 파일을 읽어서 다시 ftp서버의 /test2/디렉토리에 저장하는 것을 구현한다.  

## Maven Dependency
소스: [pom.xml](pom.xml)  
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-integration</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.integration</groupId>
			<artifactId>spring-integration-ftp</artifactId>
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
	</dependencies>
```
- spring-boot-starter-integration: 스프링 Integration을 사용하기 위한 라이브러리.  
- spring-integration-ftp: FTP Channel Adapter를 사용을 위한 라이브러리.  

## Integration Flow 생성(XML 방식)
소스: [int-ftp-channel.xml](src/main/resources/int-ftp-channel.xml)  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:int="http://www.springframework.org/schema/integration"
	xmlns:int-ftp="http://www.springframework.org/schema/integration/ftp"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/integration/ftp http://www.springframework.org/schema/integration/ftp/spring-integration-ftp.xsd
		http://www.springframework.org/schema/integration http://www.springframework.org/schema/integration/spring-integration-5.2.xsd">
	<bean id="ftpFactory"
		class="org.springframework.integration.ftp.session.DefaultFtpSessionFactory">
		<property name="host" value="localhost"></property>
		<property name="username" value="ftpuser"></property>
		<property name="password" value="init0000"></property>
	</bean>
	<int:channel id="channel"/>
	<int-ftp:inbound-channel-adapter session-factory="ftpFactory"
		channel="channel" 
		remote-directory="/test1/" 
		local-directory="source"
		delete-remote-files="true"
		preserve-timestamp="true">
		<int:poller fixed-rate="5000"/>
	</int-ftp:inbound-channel-adapter>
	<int-ftp:outbound-channel-adapter 
		session-factory="ftpFactory"
		channel="channel" 
		id="outFtp" 
		auto-create-directory="true"
		remote-directory="/test2/">
	</int-ftp:outbound-channel-adapter>
</beans>
```
- DefaultFtpSessionFactory클래스로 ftpFactory빈을 생성한다.
  - host: ftp서버
  - username: ftp계정
  - password: 비밀번호
- &lt;int:channel/&gt;: Ftp 어댑터가 사용할 채널을 선언한다.    
- &lt;int-ftp:inbound-channel-adapter/&gt;: 인바운드 Ftp 채널 어댑터  
  - session-factory: 위에서 선언한 ftpFactory빈을 등록  
  - channel: 사용할 채널 등록.
  - remote-directory: ftp서버의 원격 디렉토리
  - local-directory: ftp서버에서 가져온 파일을 저장할 로컬 디렉토리
  - delete-remote-files: true로 설정하면 로컬로 가져온 ftp서버의 파일을 삭제한다.
  - preserve-timestamp: true로 설정하면 로컬로 저장시 파일의 생성일자를 원본 파일과 동일하게 유지한다.
  - &lt;int:poller&gt;: fixed-rate=”5000” 폴링 타임을 5초로 지정

- &lt;int-ftp:outbound-channel-adapter&gt;: 아웃바운드 Ftp채널 어댑터
  - session-factory: 위에서 선언한 ftpFactory빈을 등록  
  - channel: 사용할 채널 등록.
  - auto-create-directory: true로 설정하면 ftp서버에 대상 디렉토리가 존재하지 않을 경우 자동 생성
  - remote-directory: ftp서버의 원격 디렉토리

## ImportResource설정
메인 클래스나 설정클래스에 @ImportResource로 체널 어뎁터 xml파일을 등록한다.  
소스: [Application.java](src/main/java/com/linor/singer/Application.java)  
```java
@SpringBootApplication
@ImportResource("classpath:int-ftp-channel.xml")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

## 결과 테스트
- 로컬 서버에 ftp를 설치한다.  
- ftpuser 계정을 생성한다. 
- ftpuser 계정으로 변경하여 ftpuser계정의 홈디렉토리에 test1디렉토리를 생성한다.
- test1디렉토리에 abcd.txt파일을 생성한다.
```text
안녕하세요.
테스트 내용입니다.
```
- 스프링 부트를 실행한다.  
- 프로젝트의 source디렉토리에 abct.txt파일이 저장되었는지 확인한다.
- ftpuser계정의 홈 디렉토리에 /test2/디렉토리가 생성되고 abct.txt파일이 저장되었는지 확인한다.  

## 참고 URL

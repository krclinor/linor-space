# Spring Boot에 SSL 적용하기
보통 SSL을 적용하기 위해서는 인증업체에서 제공하는 인증키를 이용하여 SSL을 적용한다.
여기서는 로컬에서 인증키를 생성하여 적용하는 방법을 설명한다.

## Spring Boot Starter를 이용한 프로젝트 생성
기존에 만든 02-01-mvc-static프로젝트를 복사하여  
02-02-mvc-ssl로 생성한다.

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
			<groupId>org.webjars</groupId>
			<artifactId>bootstrap</artifactId>
			<version>4.5.0</version>
		</dependency>
	</dependencies>
```
## 키 생성
```bash
keytool -genkey -alias linor-ssl -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
```
- -alias linor-ssl : key alias를 linor-ssl로 지정  
- -keystore keystore.p12 : key store이름을 keystore.p12로 지정  

생성된 keystore.p12파일은 /src/main/resources폴더에 저장한다.

## 설정
### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
server:
  servlet:
    context-path: /
  port: 8443
  ssl:
    enabled: true
    key-store: "classpath:keystore.p12"
    key-store-password: linor1111
    key-store-type: PKCS12
    key-alias: linor-ssl
```
사용포트는 8443으로 설정


## 결과 테스트
브라우저에서 다음 주소를 호출한다.  
https://localhost:8443

## 8080포트 접속시 8443리다이렉트 처리
내장 톰켓서버를 설정하는 클래스를 추가한다.

소스 : [TomcatConfiguration.java](src/main/java/com/linor/singer/config/TomcatConfiguration.java)  
```java
@Configuration
public class TomcatConfiguration {
	@Value("${server.port}")
	int serverPort;
	
	@Bean
	public ServletWebServerFactory servletConainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
			
		};
		
		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}
	
	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(serverPort);
		return connector;
	}
}
```
### 결과 테스트
브라우저에서 다음 주소를 호출한다.  
http://localhost:8080
그러면 https://localhost:8443 으로 이동된다.


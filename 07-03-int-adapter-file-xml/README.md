# File Channel Adapter
source디렉토리의 파일을 Transformer를 통해서 변환후 destination디렉토리에 저장하는 것을 구현해 본다.  

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
			<artifactId>spring-integration-file</artifactId>
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
- spring-integration-file: File Channel Adapter를 사용을 위한 라이브러리.  

## Transformer
변환기는 거의 사용할 일이 없으나 파일의 내용을 변경하고자 하는 경우 사용할 수 있다.  
소스: [FileTransformer.java](src/main/java/com/linor/singer/adapter/FileTransformer.java)  
```java
@Component
public class FileTransformer {
	public String transform(String filePath) throws IOException{
		String content = new String(Files.readAllBytes(Paths.get(filePath)));
		return "변환된 내용 : " + content;
	}
}
```
transform메서드가 파일의 내용을 변경하도록 한다.  
위 메서드는 파일의 내용 앞에 “Transformed: “를 추가한다.

## Integration Flow 생성(XML 방식)
소스: [int-file-channel.xml](src/main/resources/int-file-channel.xml)  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:int="http://www.springframework.org/schema/integration"
	xmlns:int-file="http://www.springframework.org/schema/integration/file"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/integration/file http://www.springframework.org/schema/integration/file/spring-integration-file.xsd
		http://www.springframework.org/schema/integration http://www.springframework.org/schema/integration/spring-integration-5.2.xsd">
	<int:channel id="inChannel"/>
	<int:channel id="outChannel"/>
	<int-file:inbound-channel-adapter id="inFile" 
			directory="source"
			channel="inChannel">
		<int:poller fixed-rate="5000"/>
	</int-file:inbound-channel-adapter>
	<int:transformer id="fileTransform"
			input-channel="inChannel"
			ref="fileTransformer"
			method="transform"
			output-channel="outChannel"/>
	<int-file:outbound-channel-adapter id="outFile"
			channel="outChannel"
			directory="destination"
			delete-source-files="false"/>
</beans>
```
- &lt;int:channel/&gt;: 입력채널인 inChannel과 출력채널인 outChannel을 선언한다.  
- &lt;int-file:inbound-channel-adapter/&gt;: 인바운드 채널 어댑터  
  - directory: 입력디렉토리의 위치를 등록.  
  - channel: 입력채널인 inChannel을 등록.
  - &lt;int:poller&gt;: fixed-rate=”5000” 폴링 타임을 5초로 지정

- &lt;int:transformer&gt;: 메시지 Transformer 는 메시지의 내용과 구조를 변경해서 리턴하는 역할을 수행한다.  
  - input-channel : 입력 채널
  - ref : Transformer빈
  - method : Transformer빈의 메서드
  - output-channel: 출력 채널

- &lt;int-file:outbound-channel-adapter&gt;: 아웃바운드 채널 어댑터
  - directory: 출력디렉토리의 위치를 등록.  
  - channel: 출력채널인 outChannel을 등록.
  - delete-source-files: true로 설정하면 대상 디렉토리로 파일이 저장된 후 원본 소스파일을 삭제한다.


## ImportResource설정
메인 클래스나 설정클래스에 @ImportResource로 체널 어뎁터 xml파일을 등록한다.  
소스: [Application.java](src/main/java/com/linor/singer/Application.java)  
```java
@SpringBootApplication
@ImportResource("classpath:int-file-channel.xml")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

## 결과 테스트
소스: [test1](source/test1)
```text
test contents..
```

스프링 부트를 실행한다.  
결과 프로젝트의 destination/test1파일이 생성되고 내용은 다음과 같다.  
```text
변환된 내용 : test contents..
```

## 참고 URL

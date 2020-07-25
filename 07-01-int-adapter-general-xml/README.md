# Generic Channel Adapter
Channel Adapter 는 메시지 채널을 다른 시스템이나 전송층으로 연결해주는 엔드포인트이다.  

일반적인 인바운드 채널 어뎁터에서 랜덤으로 문자열 생성하고, 아웃바운드 채널 어뎁터에서 콘솔에 해당 내역을 출력하도록 한다.  
## Maven Dependency
소스: [pom.xml](pom.xml)  
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-integration</artifactId>
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
spring-boot-starter-integration라이브러리가 스프링 Integration을 사용하기 위한 라이브러리 이다.

## Inbound Channel Adapter
문자열 메시지를 생성하는 컴포넌트를 생성한다.  
소스: [MessageProducer.java](src/main/java/com/linor/singer/adapter/MessageProducer.java)
```java
@Component
public class MessageProducer {
	String[] array = {"첫 번째 줄!", "두 번째 줄!", "세 번째 줄!"};
	public String produce() {
		return array[new Random().nextInt(3)];
	}
}
```

## Outbound Channel Adapter
문자열 메시지를 로그에 출력하는 컴포넌트를 생성한다.  
소스: [MessageConsumer.java](src/main/java/com/linor/singer/adapter/MessageConsumer.java)
```java
@Slf4j
@Component
public class MessageConsumer {
	public void consume(String message) {
		log.info("결과 메시지: " + message);
	}
}
```

## XML 설정파일 생성
Spring Integration을 설정하는 xml을 생성한다.
소스: [in-out-channel.xml](src/main/resources/in-out-channel.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:int="http://www.springframework.org/schema/integration"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/integration http://www.springframework.org/schema/integration/spring-integration-5.2.xsd">
	<int:channel id="channel"/>
	<int:inbound-channel-adapter id="inAdapter" 
		channel="channel"
		ref="messageProducer"
		method="produce">
		<int:poller fixed-rate="1000"/>
	</int:inbound-channel-adapter>
	<int:outbound-channel-adapter channel="channel" id="outAdapter"
		ref="messageConsumer" method="consume">
	</int:outbound-channel-adapter>
</beans>
```
- <int:channel id="channel"/> : 메시지를 전송할 채널
- &lt;int:inbound-channel-adapter&gt;: Inbound Channel Adapter로 설정하며, 참조 Bean은 messageProducer이고 Bean의 처리 메서드는 produce로 설정한다.
- <int:poller fixed-rate="1000"/> : 1초마다 체널 어뎁터에서 체널로 메시지를 보낸다.  
  - fixed-rate: 이전 호출 후 다시 호출하는데 걸리는 시간을 설정  
  - fixed-delay: 이전 호출이 처리 완료된 후 다시호출하기 위대 대기하는 시간을 설정
- &lt;int:outbound-channel-adapter&gt;: Outbound Channel Adapter를 설정하며, 참조 Bean은 messageConsumer이고, Bean의 처리 메서드는 consume으로 설정한다.

## ImportResource설정
소스: [Application.java](src/main/java/com/linor/singer/Application.java)  
```java
@SpringBootApplication
@ImportResource("classpath:in-out-channel.xml")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```
스프링부트 메인 클래스에 @ImportResource("classpath:in-out-channel.xml")를 선언하여 설정한 xml파일을 읽어오도록 한다.  

## 결과
```log
2020-07-25 15:17:44.589  INFO 98318 --- [ask-scheduler-1] o.s.i.h.s.MessagingMethodInvokerHelper   : Overriding default instance of MessageHandlerMethodFactory with provided one.
2020-07-25 15:17:44.596  INFO 98318 --- [ask-scheduler-1] c.linor.singer.adapter.MessageConsumer   : 결과 메시지: 두 번째 줄!
2020-07-25 15:17:44.599  INFO 98318 --- [  restartedMain] com.linor.singer.Application             : Started Application in 1.197 seconds (JVM running for 1.728)
2020-07-25 15:17:45.580  INFO 98318 --- [ask-scheduler-1] c.linor.singer.adapter.MessageConsumer   : 결과 메시지: 두 번째 줄!
```

# JPA 리액티브 프로그래밍

## 리액티브 프로그래밍이란?
위키피디아에 정의된 리액티브 프로그래밍은 데이터 흐름과 변화 전파에 중점을 둔 프로그래밍 패러다임이다. 

### 명령형 프로그래밍 vs 리액티브 프로그래밍
- 명령형 프로그래밍: 작성된 코드가 정해진 순서대로 실행됨
- 리액티브 프로그래밍: 데이터 흐름을 먼저 정의하고 데이터가 변경되었을 때 연관되는 함수나 수식이 업데이트 되는 방식

### 리액티브 선언문
리액티브 시스템은:  
- 응답성(Responsive): " 시스템 이 가능한 한 즉각적으로 응답하는 것을 응답성이 있다고 합니다. 응답성은 사용자의 편의성과 유용성의 기초가 되지만, 그것뿐만 아니라 문제를 신속하게 탐지하고 효과적으로 대처할 수 있는 것을 의미합니다. 응답성 있는 시스템은 신속하고 일관성 있는 응답 시간을 제공하고, 신뢰할 수 있는 상한선을 설정하여 일관된 서비스 품질을 제공합니다. 이러한 일관된 동작은 오류 처리를 단순화하고, 일반 사용자에게 신뢰를 조성하고, 새로운 상호작용을 촉진합니다.

- 탄력성(Resilient): 시스템이 장애에 직면하더라도 응답성을 유지하는 것을 탄력성이 있다고 합니다. 탄력성은 고가용성 시스템, 미션 크리티컬 시스템에만 적용되지 않습니다. 탄력성이 없는 시스템은 장애가 발생할 경우 응답성을 잃게 됩니다. 탄력성은 복제, 봉쇄, 격리, 위임에 의해 실현됩니다. 장애는 각각의 구성 요소 에 포함되며 구성 요소들은 서로 분리되어 있기 때문에 이는 시스템이 부분적으로 고장이 나더라도, 전체 시스템을 위험하게 하지 않고 복구 할 수 있도록 보장합니다. 각 구성 요소의 복구 프로세스는 다른(외부의) 구성 요소에 위임되며 필요한 경우 복제를 통해 고가용성이 보장됩니다. 구성 요소의 클라이언트는 장애를 처리하는데에 압박을 받지 않습니다.

- 유연성(Elastic): 시스템이 작업량이 변화하더라도 응답성을 유지하는 것을 유연성이라고 합니다. 리액티브 시스템은 입력 속도의 변화에 따라 이러한 입력에 할당된 자원을 증가시키거나 감소키면서 변화에 대응합니다. 이것은 시스템에서 경쟁하는 지점이나 중앙 집중적인 병목 현상이 존재하지 않도록 설계하여, 구성 요소를 샤딩하거나 복제하여 입력을 분산시키는 것을 의미합니다. 리액티브 시스템은 실시간 성능을 측정하는 도구를 제공하여 응답성 있고 예측 가능한 규모 확장 알고리즘을 지원합니다. 이 시스템은 하드웨어 상품 및 소프트웨어 플랫폼에 비용 효율이 높은 방식으로 유연성 을 제공합니다.

- 메시지 구동(Message Driven): 리액티브 시스템은 비동기 메시지 전달에 의존하여 구성 요소 사이에서 느슨한 결합, 격리, 위치 투명성 을 보장하는 경계를 형성합니다. 이 경계는 장애 를 메시지로 지정하는 수단을 제공합니다. 명시적인 메시지 전달은 시스템에 메시지 큐를 생성하고, 모니터링하며 필요시 배압 을 적용함으로써 유연성을 부여하고, 부하 관리와 흐름제어를 가능하게 합니다. 위치 투명 메시징을 통신 수단으로 사용하면 단일 호스트든 클러스터를 가로지르든 동일한 구성과 의미를 갖고 장애를 관리할 수 있습니다. 논블로킹 통신은 수신자가 활성화가 되어 있을 때만 자원 을 소비할 수 있기 때문에 시스템 부하를 억제할 수 있습니다.

## 설정

### 메이븐 의존성 
Spring Boot Starter에서 Lombok, SpringBoot DevTools, Spring Data JPA, PostgreSQl Driver, Sprin Reactive Web을 추가하여 프로젝트를 생성한다.  
소스 : [pom.xml](pom.xml)  
```xml
	<dependencies>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>io.projectreactor</groupId>
			<artifactId>reactor-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
	</dependencies>
```
추가 의존성 라이브러리로 hibernate-types-52를 추가하여 Hibernate CamelCase를 SnakeCase로 변경할 수 있도록 한다.



## 참고사이트
- https://www.charlezz.com/?p=189
- https://www.reactivemanifesto.org/ko
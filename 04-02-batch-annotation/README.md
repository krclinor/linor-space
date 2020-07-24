# Spring Batch
## 기본 개념
스프링 배치는 벡엔드의 배치처리 기능을 구현하는 데 사용하는 프레임워크이다.  
배치의 일반적인 시나리오는 다음과 같은 3단계로 이루어진다.  
1. 읽기(read) : 데이터 저장소(일반적으로 데이터베이스)에서 특정 데이터 레코드를 읽기.
2. 처리(processing) : 원하는 방식으로 데이터 가공/처리.
3. 쓰기(write) : 수정된 데이터를 다시 저장소(데이터베이스)에 저장.

다음 그림은 스프링의 배치처리 관련 객체의 관계이다.  
![](images/image01.png)
- Job과 Step은 1:M
- Step과 ItemReader, ItemProcessor, ItemWriter 1:1
- Job이라는 하나의 큰 일감(Job)에 여러 단계(Step)을 두고, 각 단계를 배치의 기본 흐름대로 구성한다.

Job
Job은 배치 처리 과정을 하나의 단위로 만들어 포현한 객체다. 또한 전체 배치 처리에 있어 항상 최상단 계층에 있다.  
위에서 하나의 Job(일감) 안에는 여러 Step(단계)이 있다고 설명했던 바와 같이 스프링 배치에서 Job 객체는 여러 Step 인스턴스를 포함하는 컨테이너 이다.  

Step  
Step은 실직적인 배치 처리를 정의하고 제어 하는데 필요한 모든 정보가 있는 도메인 객체이다. Job을 처리하는 실질적인 단위로 쓰인다.  
모든 Job에는 1개 이상의 Step이 있어야 한다.

ItemReader
ItemReader는 Step의 대상이 되는 배치 데이터를 읽어오는 인터페이스이다.  
File, Xml Db등 여러 타입의 데이터를 읽어올 수 있다.  

ItemProcessor
ItemProcessor는 ItemReader로 읽어 온 배치 데이터를 변환하는 역할을 수행한다. 이것을 분리하는 이유는 다음과 같다.  
- 비즈니스 로직의 분리 : ItemWriter는 저장 수행하고, ItemProcessor는 로직 처리만 수행해 역할을 명확하게 분리.  
- 읽어온 배치 데이터와 씌여질 데이터의 타입이 다를 경우에 대응.

ItemWriter  
ItemWriter는 배치 데이터를 저장한다.  
일반적으로 DB나 파일에 저장합니다.  
ItemWriter도 ItemReader와 비슷한 방식을 구현한다. 제네릭으로 원하는 타입을 받고 write() 메서드는 List를 사용해서 저장한 타입의 리스트를 매게변수로 받는다.

## Spring Boot Starter를 이용한 프로젝트 생성
Spring Boot -> Spring Starter Project로 생성한다.  

### 의존성 라이브러리
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>${mybatis-spring-boot-starter.version}</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
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
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-batch</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.batch</groupId>
			<artifactId>spring-batch-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
	</dependencies>
```
spring-boot-starter-batch를 추가함으로써 스프링 배치를 사용할 수 있다.  
사용할 데이터베이스는 postgreSql로 하며, orm은 Mybatis를 이용한다.  
빠른 코딩과 소스의 깔끔함을 위하여 lombok를 사용한다.  

## 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
#### 로그 설정
logging:
  level: 
    root: INFO
    com:
        linor: 
          app: TRACE
         
spring:
#데이타베이스 환경설정
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: always

  #Spring Batch 설정
  batch:
    #배치 데이타베이스 초기화(Spring Batch에 사용되는 테이블 생성)
    initialize-schema: always  
    job:
      #Bootstrap시 Job을 자동으로 실행하지 않도록 false로 함
      enabled: false

#마이바티스 설정
mybatis:
  mapper-locations: classpath*:/**/*Dao.xml
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true

files:
  input-file: files/input.csv
  output-file: files/output.csv      
```
- files:input-file: files/input.csv - 스프링 배치에서 입력파일로 사용하기 위해 설정한다. 파일 내용은 다음과 같다.  
```text
id, firstName, lastName, birthDate
1, 영미, 이, 1978-06-28
2, 현화, 노, 2000-11-01
3, 혜경, 민, 1978-01-03
```
- files:output-file: files/output.csv  – 스프링 배치에서 출력 파일로 사용하기 위해 설정
- spring:batch:initialize-schema: always – 스프링 배치를 사용하기 위해 필요한 테이블 설치를 위하여 설정한다.
-- BATCH_JOB_INSTANCE
-- BATCH_JOB_EXECUTION
-- BATCH_JOB_EXECUTION_CONTEXT
-- BATCH_JOB_EXECUTION_PARAMS
-- BATCH_STEP_EXECUTION
-- BATCH_STEP_EXECUTION_CONTEXT

### 데이타베이스 초기화 파일 생성
소스 : [schema.sql](src/main/resources/schema.sql)  
```sql
drop table if exists singer cascade;
create table singer(
  id serial primary key,
  first_name varchar(60) not null,
  last_name varchar(60) not null,
  birth_date date,
  constraint singer_uq_01 unique(first_name, last_name)
);
```

소스 : [data.sql](src/main/resources/data.sql)  
```sql
insert into singer(first_name, last_name, birth_date)
values
('종서','김','19701209'),
('건모','김','19990712'),
('용필','조','19780628'),
('진아','태','20001101');
```

## Domain 클래스 생성
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)
```java
@Data
@Builder
public class Singer {
	@Builder.Default
	private Integer id = null;
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
}
```
@Data는 lombok어노테이션으로 소스 컴파일시 자동으로 get/set, equals, hashCode, toString매서드를 생성하여 코딩을 깔끔하게 할 수 있다.
@NoArgsConstructor는 lombok어노테이션으로 매개변수 없는 생성자를 만들어준다.
@AllArgsConstructor는 lombok어노테이션으로 Singer의 모든 프로퍼티를 포함하는 생성자를 만들어 준다.

## DAO인터페이스 생성
소스 : [SingerDao.java](src/main/java/com/linor/singer/dao/SingerDao.java)  
```java
@Mapper
public interface SingerDao {
	public void insert(Singer singer);
	public List<Singer> findAll();
}
```
@Mapper는 인터페이스가 Mybatis의 Mapper인터페이스 임을 알려준다.  
csv파일에서 대량의 데이터를 singer테이블에 입력하기 위한 insert메서드와 singer테이블의 모든 레코드를 파일에 저장하기 위해 findAll메서드를 정의한다.  

### SingerDao인터페이스 구현
소스 : [SingerDao.xml](src/main/resources/com/linor/singer/dao/SingerDao.xml)  
```xml
<mapper namespace="com.linor.singer.dao.SingerDao">
<insert id="insert" parameterType="com.linor.singer.domain.Singer">
	insert into singer(id, first_name, last_name, birth_date)
	values(default, #{firstName}, #{lastName}, #{birthDate})
</insert>
<select id="findAll" resultType="com.linor.singer.domain.Singer">
	select * from singer
</select>
</mapper>
```

## FieldSetMapper 구현
LineMapper에서 사용할 FieldSetMapper를 구현한다.  
소스 : [SingerFieldSetMapper.java](src/main/java/com/linor/singer/batch/SingerFieldSetMapper.java)  
```java
public class SingerFieldSetMapper implements FieldSetMapper<Singer> {

	@Override
	public Singer mapFieldSet(FieldSet fieldSet) throws BindException {
		return Singer.builder()
				.firstName(fieldSet.readString(1))
				.lastName(fieldSet.readString(2))
				.birthDate(LocalDate.parse(fieldSet.readString(3)))
				.build();
	}
}
```
FieldSetMapper는 파일에서 읽어온 라인의 각 필드를 객체의 맴버변수 대입하기 위해 사용한다.  

## ItemProcessor구현
ItemProcessor는 필터링효과 또는 중간에 데이터내용 변경등이 필요한 경우에 사용한다.  
소스 : [SingerProcessor.java](src/main/java/com/linor/singer/batch/SingerProcessor.java)  
```java
@Service
@Slf4j
public class SingerProcessor implements ItemProcessor<Singer, Singer> {

	@Override
	public Singer process(Singer item) throws Exception {
		String firstName = item.getFirstName();
		if("리노".equals(firstName)) {
			return null;
		}
		item.setFirstName(firstName + "님");
		log.info("{} 에서 {}로 변환됨.", firstName, item.getFirstName());
		return item;
	}
}
```
null값을 리턴하는 경우 해당 객체는 ItemWriter에서 처리하지 않는다.  
객체의 내용을 Reader에서 읽은 내용을 변경할 경우에도 사용된다.   

## ItemWriter 구현
소스 : [SingerDbWriter.java](src/main/java/com/linor/singer/batch/SingerDbWriter.java)  
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class SingerDbWriter implements ItemWriter<Singer> {

	private final SingerDao singerDao;
	
	@Override
	public void write(List<? extends Singer> items) throws Exception {
		log.info("저장될 가수: {}", items);
		items.forEach(singer -> {
			singerDao.insert(singer);
			log.info("저당된 가수: {}", singer.toString());
		});
	}
}
```

## Spring Batch 구성 클래스 생성
소스 : [BatchConfig.java](src/main/java/com/linor/singer/config/BatchConfig.java)  
```java
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final SqlSessionFactory sqlSessionFactory;
	private final SingerProcessor singerProcessor;
	private final SingerDbWriter singerDbWriter;
...
```
@Configuration은 구성설정용 클래스임을 알린다.
@EnableBatchProcessing은 스프링 Batch처리용임을 알린다.
@RequiredArgsConstructor은 lombok어노테이션으로 private final인 프로퍼티들을 생성자메개변수로 하여 생성자를 만든다.

### Job
```java
	@Bean
	public Job job3() {
		return jobBuilderFactory.get("ETL-Load3")
				.incrementer(new RunIdIncrementer())
				.start(step1())
				.next(step2())
				.build();
	}
```
최종 처리할 job으로, step1처리 후 step2를 처리하도록 한다.

### step1(파일 -> 데이터베이스)
step1은 파일의 각 라인을 읽어서 데이타베이스에 저장한다.  
```java
	@Bean
	Step step1() {
		return stepBuilderFactory.get("ETL-file-load")
				.<Singer, Singer>chunk(100)
				.reader(fileItemReader(null))
				.processor(singerProcessor)
				.writer(singerDbWriter)
				//.writer(dbWriter())
				.build();
	}
```
- chunk는 step에서 한번에 처리할 라인수를 지정한다. 파일에서 100라인을 읽어서 처리하도록 한다.  
- reader()에 fileItemReader빈을 설정하고 파라미터를 null로 한다.  
- writer(singerDbWriter)는 writer를 별도 빈으로 구현한 경우
- writer(dbWriter())는 클래스 내에 빈을 설정하여 처리한 경우임.

#### fileItemReader 빈
```java
	@Bean
	public FlatFileItemReader<Singer> fileItemReader(@Value("${files.input-file}") String path){
		FlatFileItemReader<Singer> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource(path));
		flatFileItemReader.setLinesToSkip(1);//첫 라인 건너뜀
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}
```
- 빈의 path값이 null인 경우 application.yml에서 선언한 file.input-file값을 삽입한다.  
- flatFileItemReader.setLinesToSkip(1)은 파일의 첫 번째 라인이 해더이므로 읽지 않고 건너뛰도록 한다.  
- flatFileItemReader.setLineMapper(lineMapper())은 라인매퍼 빈을 선언한여 파일에서 읽은 라인을 객체로 변환하도록 한다.  

#### dbWriter 빈
```java
	@Bean
	public MyBatisBatchItemWriter<Singer> dbWriter(){
		final MyBatisBatchItemWriter<Singer> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(sqlSessionFactory);
		writer.setStatementId("com.linor.singer.dao.SingerDao.insert");
		return writer;
	}
```
데이터베이스에 저장하기 위해 MyBatisBatchItemWriter를 사용하여 처리한 경우로 MybatisBatchItemWriter를 생성하여 sqlSessionFactory 및 statementId를 설정한다.

### step2(데이터베이스 -> 파일)
step2는 데이타베이스에서 여러 레코드를 읽어서 파일에 저장한다.  
```java
	@Bean
	Step step2() {
		return stepBuilderFactory.get("ETL-file-write")
				.<Singer, Singer>chunk(10)
				.reader(dbReader())
				.writer(fileWriter(null))
				.build();
	}
```
- chunk(10): 리더에서 10라인 단위로 읽어 처리하도록 한다.  
- reader(dbReader()): 리더 빈을 dbReader빈으로 선언한다.  
- writer(fileWriter(null)): 저장처리 빈을 fileWriter로 선언한다.  

#### dbReader 빈
```java
	@Bean
	public MyBatisCursorItemReader<Singer> dbReader(){
		final MyBatisCursorItemReader<Singer> reader = new MyBatisCursorItemReader<>();
		reader.setSqlSessionFactory(sqlSessionFactory);
		reader.setQueryId("com.linor.singer.dao.SingerDao.findAll");
		return reader;
	}
```
Mybatis에서는 ItemReader를 상속받은 MyBatisCursorItemReader를 이용하여 처리할 수 있다.  
이외에도 MyBatisPagingItemReader도 제공한다.  

#### fileWriter 빈
```java
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Bean
	public FlatFileItemWriter<Singer> fileWriter(@Value("${files.output-file}") String path){
		final FlatFileItemWriter<Singer> writer = new FlatFileItemWriter<>();
		writer.setResource(new FileSystemResource(path));
		writer.setHeaderCallback(headerWriter -> headerWriter.append("id, firstName, lastName, birthDate"));
		writer.setLineAggregator(item -> item.getId() + ","
				+ item.getFirstName() + "," 
				+ item.getLastName() + ","
				+ item.getBirthDate().format(formatter));
		return writer;
	}
```
setResource를 이용하여 저장할 파일을 지정하고, setHeaderCallback으로 해더라인을 등록한다.
setLineAggregator를 이용하여 라인값을 설정한다.

## Spring Batch처리를 위한 RestAPI 만들기
소스 : [LoadController.java](src/main/java/com/linor/singer/controller/LoadController.java)  
```java
@RestController
@Slf4j
@RequiredArgsConstructor
public class LoadController {
	
	private final SingerDao dao;
	
	private final JobLauncher jobLauncher;
	
	@Qualifier("job2")
	private final Job job2;
	
	@GetMapping("/load")
	public BatchStatus load() throws JobExecutionAlreadyRunningException,
		JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException{
		Map<String, JobParameter> maps = new HashMap<String, JobParameter>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job2, parameters);
		
		log.info("JobExecuteion: " + jobExecution.getStatus());
		log.info("Batch is Running...");
		while(jobExecution.isRunning()) {
			log.info(".......");
		}
		return jobExecution.getStatus();
	}
	
	@GetMapping("/list")
	public List<Singer> list(){
		return dao.findAll();
	}
}
```

## Spring Batch Job Scheduling
소스 : [ScheduleConfig.java](src/main/java/com/linor/singer/config/ScheduleConfig.java)  
```java
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduleConfig {
	private final JobLauncher jobLauncher;
	
	@Qualifier("job1")
	private final Job job1;
	
	@Scheduled(fixedDelay = 50000)
	public void run() throws Exception{
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution execution = jobLauncher.run(job1, parameters);
		log.info("스케줄 처리 결과 상태: " + execution.getStatus());
	}
}
```
@EnableScheduling을 선언함으로써 스케줄 처리를 스프링프레임워크에 알린다.  
스프링 배치를 처리하기 위하여 JobLauncher를 스프링으로부터 인젝션받아 사용한다.  
@Scheduled(fixedRate = 5000)로 5초마다 스케줄러가 작동하도록 한다. cron등 다양한 방법으로 스케줄을 설정할 수 있다.  
이제 스프링 부트를 실행하면 5초마다 자동 실행됨을 확인한다.  

## 참고 사이트 
- https://cheese10yun.github.io/spring-batch-basic/
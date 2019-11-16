# Mybatis를 활용한 Spring Boot 보안개발
이 프로젝트에서 설명하고자 하는 것은 사용자가 특정URL에 접근하려 할 때 인증을 거쳤는지, 접근권한이 있는지를 체크하는 것을 구현해 본다.  
사용자와 접근권한을 데이타베이스로 관리하며, Mybatis로 데이타베이스 연동하도록 한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
Spring boot Starter로 프로젝트 생성시 패키징은 war로 설정한다.
```xml
	<packaging>war</packaging>
```

### 의존성 라이브러리
Spring initializer로 생성시 기본 dependency는 Web, DevTools, Lombok, postgresSQL Driver, Spring Security를 선택한다. 
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
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
 		<!-- JSTL for JSP -->
		<dependency>
			<groupId>javax.servlet</groupId>
 			<artifactId>jstl</artifactId>
 		</dependency>
		<!-- Need this to compile JSP -->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-taglibs</artifactId>
		</dependency>
 		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.1.1</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Optional for static content. bootstrap CSS -->
		<dependency>
			<groupId>org.webjars</groupId>
			<artifactId>bootstrap</artifactId>
			<version>4.3.1</version>
		</dependency>
 	</dependencies>
```
프로젝트 생성 후 pom.xml에 JSP사용을 위해 tomcat-jasper를 추가하고,    
bootstrap을 추가한다.  

## 설정
### 데이타베이스처리를 위한 sql문 생성
#### 테이블 생성을 위한 스키마 스크립트
소스 : [schema.sql](src/main/resources/schema.sql)
```sql
set search_path to singer;

drop table if exists user_role cascade;
drop table if exists users cascade;
drop table if exists roles cascade;

create table users(
	id varchar(20) primary key,
	name varchar(100) not null,
	email varchar(100),
	password varchar(255)
);

create table roles(
	id varchar(20) primary key,
	name varchar(255) not null
);

create table user_role(
	user_id varchar(20) references users(id) on delete cascade,
	role_id varchar(20) references roles(id) on delete cascade,
	primary key(user_id, role_id)
);
```
스프링 웹보안을 위해서는 사용자와 역할을 관리하기위한 테이블이 필요하다.  
USERS는 사용자정보를 관리하기 위한 테이블로, ID, 이름, 이메일, 비밀번호를 관리한다.  
ROLES는 권한을 관리하기 위한테이블 이다.  
USER_ROLE은 사용자에게 부여한 권한을 관리하기 위한 테이블이다.  

#### 기초데이타 적제를 위한 데이타 스크립트
소스 : [data.sql](src/main/resources/data.sql)
```sql
insert into users(id, name, email, password) values
('admin', '관리자', 'admin@gmail.com','$2a$10$i5PIsD4ak2IYV3aVhps4XuIF2bvKi54JjmItJ/qGz2ogVJjE/ycDy'),
('linor', '리노', 'linor@gmail.com', '$2a$10$Z6wZsEYowSJRTPoRNsIiRO68817rTLeOLlnwcnQ2LCHZKdsDgU65y'),
('user', '데모 사용자', 'user@gmail.com', '$2a$10$GmyV4/PqvfgcSHgTX6WYBeeY9STc.rvSTZAmYrMbqhW1dJZm6eLAe');

insert into roles(id, name) values
('ROLE_ADMIN', '관리자 권한'),
('ROLE_USER', '사용자 권한');

insert into user_role(user_id, role_id) values
('admin', 'ROLE_ADMIN'),
('admin', 'ROLE_USER'),
('user', 'ROLE_USER');
```
사용자, 권한를 테스트 하기 위해 기본 데이타를 테이블에 추가하는 데이타 스크립트이다.  
사용자 데이블에 admin, linor, user를 추가하여 테스트 본다.  
비밀번호는 WebSecurityConfig에서 설정한 BCryptPasswordEncoder를 이용하여 인코딩한 비밀번호로 설정한다. 
비밀번호를 변경하려면 테스트 프로그램인 ApplicationsTest클래스에서 비밀번호를 수정하여 JUnit테스트를 실행하면 콘솔에 나타나도록 하였다.
테스트 프로그램 [ApplicationTets.java](src/test/java/com/linor/singer/ApplicationTests.java)  
```java
	@Test
	public void testPassword() {
		log.info("linor -> 인코딩된 암호: " + encoder.encode("linor"));
		log.info("user -> 인코딩된 암호: " + encoder.encode("user"));
	}
```

### 어플리케이션 설정
소스 : [application.yml](src/main/resources/application.yml)  
```yml
#로거
logging:
  pattern:
    console: '%-5level %logger{0} - %msg%n'
  level:
    root: info
    com.linor.singer.dao: trace

spring:
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
    locale: ko_KR
  messages: #메시시 프로퍼티 설정
    basename: org/springframework/security/messages
    cache-duration: -1
    encoding: UTF-8
    fallback-to-system-locale: true
  datasource: #데이타소스
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: always

mybatis:
  mapper-locations: classpath*:/**/dao/*.xml
  type-aliases-package: com.linor.singer.domain
  configuration.map-underscore-to-camel-case: true
  
welcome.message : 안녕하세요!! 
```
데이타베이스 설정을 이전에 작성한 마이바티스 관련 프로젝트에서 설명되어 있으므로 생략한다.  

### MVC 뷰 컨트롤러 생성
여기서는 컨트롤러의 구현방법은 가능한 생략하고 접근 URL만 구현하기 위해 WebMvcConfigurer를 이용하여 뷰 컨트롤러를 만든다.  
뷰컨트롤러는 컨트롤러에서 로직을 처리하는 것이 없을 경우 바로 JSP로 넘기는 역할을 한다.  
소스 :[MvcConfig.java](src/main/java/com/linor/singer/config/MvcConfig.java)  
```java
@Configuration
public class MvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/admin/home").setViewName("adminhome");
		registry.addViewController("/user/linor/home").setViewName("linorsHome");
		registry.addViewController("/accessDenied").setViewName("403");
		registry.addViewController("/logoutSuccess").setViewName("logoutSuccess");
	}

}
```
/login은 인증처리용 URL이며, 로그인 실패시 /accessDenied페이지로 이동된다.  
/logoutSuccess는 로그아웃 후 이동할 페이지를 설정한다.  
/home은 인증후 일반사용자가 접속할 수 있는 페이지로 사용할 예정이며, /admin/home은 ROLE_ADMIN권한을 가진 사용자만 접근할 수 있도록 한다.  

### 페이지별 보안 설정
소스 : [WebSecurityConfig.java](src/main/java/com/linor/singer/config/WebSecurityConfig.java)
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
	
	protected void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()
				.antMatchers("/", "/logoutSuccess")
					.permitAll()
				.antMatchers("/admin/**")
//					.hasAuthority("ADMIN")
//					.hasRole("ADMIN")//데이타베이스에는 ROLE_ADMIN으로 저장되어 있어야 함
//					.hasAnyAuthority("ADMIN")
					.hasAnyRole("ADMIN")//데이타베이스에는 ROLE_ADMIN으로 저장되어 있어야 함
				.antMatchers("/user/{userId}/home")
					.access("@myWebSecurity.checkUserId(authentication, #userId, request)")
				.anyRequest()
					.authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
				.defaultSuccessUrl("/home")
				.failureUrl("/login?error")
				.permitAll()
			.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//				.logoutSuccessUrl("/login?logut")
				.logoutSuccessUrl("/logoutSuccess")
				.permitAll()
			.and()
			.exceptionHandling()
				.accessDeniedPage("/accessDenied");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(
					HttpMethod.GET, 
					"/favicon.ico", "/*.html", "/**/*.html",
					"/webjars/**", "/css/**", "/fonts/**", "/js/**", "/images/**");
	}
}
```
스프링프레임워크의 보안을 사용하기 위해 @EnaleWebSecurity를 선언하고, WebSecurityConfigurerAdapter를 상속받은 WebSecurityConfig클래스를 만든다.  

로그인시 사용자 비번체크 및 권한 체크를 JDBC를 통해 처리하도록 하기 위해 userDetailsService를 선언한다.  
사용자 로그인시 사용자가 입력한 비밀번호를 인코딩하기 위해 passwordEncoder를 선언한다.    
```java
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
```

다음은 URL별로 권한을 설정한다.
루트 페이지와 로그아웃 성공후 페이지는 인증을 하지 않더라도 접속이 가능하도록 permitAll를 설정한다.    
```java 
	protected void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()
				.antMatchers("/", "/logoutSuccess")
					.permitAll()
```

/admin/으로 시작하는 모든 페이지는 ROLE_ADMIN권한을 가진 사용자만 접근할 수 있도록 한다. 
```java
				.antMatchers("/admin/**")
//					.hasAuthority("ROLE_ADMIN")
//					.hasRole("ADMIN")//데이타베이스에는 ROLE_ADMIN으로 저장되어 있어야 함
//					.hasAnyAuthority("ROLE_ADMIN")
					.hasAnyRole("ADMIN")//데이타베이스에는 ROLE_ADMIN으로 저장되어 있어야 함
```
Authority를 이용하여 설정하는 경우에는 데이타베이스에 저장된 권한명과 동일하게 설정한다.  
Role을 이용하여 설정하는 경우에는 스프링프레임워크에서 권한명 앞에 ROLE_을 자동으로 추가한다.    

스프링에서 제공하는 권한체크로 권한을 체크하기 어려운 경우 별도의 빈을 생성하여 체크할 수 있다.  
```java
				.antMatchers("/user/{userId}/home")
					.access("@myWebSecurity.checkUserId(authentication, #userId, request)")
```
/user/로 시작하는 URL의 경우 별도의 빈(myWebSecurity)의 checkUserId메서드에서 체크하도록 한다.  
소스: [MyWebSecurity.java](src/main/java/com/linor/singer/serviceImpl/MyWebSecurity.java)
```java
@Service
public class MyWebSecurity {
	public boolean checkUserId(Authentication auth, String userId, HttpServletRequest request) {
		if(userId.equals(auth.getName())) {
			return true;
		}
		return false;
	}
}
```
  
위에서 설정하지 않은 나머지 URL은 인증체크를 하도록 한다.  
```java
				.anyRequest()
					.authenticated()
```

다음은 인증처리를 설정한다.  
```java
			.formLogin()
				.loginPage("/login")
//				.usernameParameter("username")
//				.passwordParameter("password")
				.defaultSuccessUrl("/home")
				.failureUrl("/login?error")
				.permitAll()
```
loginPage에 로그인화면을 제공하는 URL을 등록한다.  
로그인시 사용될 사용자명과 비밀번호 파라미터는 username과 password가 디폴드이다. 만일이 파라미터명을 변경하려면 usernameParameter와 passwordParameter를 선언하여 사용할 수 있다.  
defaultSuccessUrl은 로그인 성공 후 이동할 페이지가 정해지지 않은 경우 디폴트 Url을 지정할 수 있다.  
로그인 실패시 이동할 페이지 설정을 위해 failureUrl을 사용한다. 설정하지 않으면 디폴트는 /login?error이다.    
인증에 필요한 모든 URL은 permitAll로 설정한다.  
 
로그인 후 로그아웃을 위한 URL을 설정한다.  
```java
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//				.logoutSuccessUrl("/login?logut")
				.logoutSuccessUrl("/logoutSuccess")
```
브라우저에서 /logout을 호출하면 로그아웃 되도록 한다.  
로그아웃 후 이동할 페이지를 설정하기 위해 logoutSuccessurl을 선언한다. 디폴트는 /login?logout이다.  

이미지, 자바스크립트, CSS등은 정적 컨텐츠로 호출시 마다 보안체크를 하면 성능에 문제가 발생할 수 있다.  
이를 방지하기 위해 이러한 페이지들은 보안을 건너뛰도록 설정한다.  
```java
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(
					HttpMethod.GET, 
					"/favicon.ico", "/*.html", "/**/*.html",
					"/webjars/**", "/css/**", "/fonts/**", "/js/**", "/images/**");
	}
```

### UserDetailsService 구현
로그인한 사용자의 인증과 권한체크를 위해서 UserDetailsService를 구현한다.  
이를 구현하기 위해 먼저 데이타베이스를 처리하기 위한 도메인 클래스, Mybatis인터페이스 구현 매퍼등을 만든다.  

다음은 사용자 정보를 담을 MyUser클래스를 생성한다.  
소스: [MyUser.java](src/main/java/com/linor/singer/domain/MyUser.java)
```java
@Data
public class MyUser {
	private String id;
	private String name;
	private String email;
	private String password;
}
```

사용자의 권한정보를 담을 Role클래스를 생성한다.  
소스: [Role.java](src/main/java/com/linor/singer/domain/Role.java)
```java
@Data
public class Role {
	private String id;
	private String name;
}
```

데이타베이스 연동에 필요한 매퍼 인터페이스를 생성한다.  
소스: [UserDao.java](src/main/java/com/linor/singer/dao/UserDao.java)
```java
@Mapper
public interface UserDao {
	MyUser findById(String id);
	List<Role> listRolesByUser(MyUser user);
}
```

매퍼 인터페이스를 구현할 XML구현체를 생성한다.  
소스: [UserDao.xml](src/main/resources/com/linor/dao/UserDao.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.linor.singer.dao.UserDao">
	<select id="findById" parameterType="string" resultType="MyUser">
		select *
		from users
		where id = #{id}
	</select>
	<select id="listRolesByUser" parameterType="MyUser" resultType="Role">
		select *
		from roles as r
		inner join user_role as ur
			on ur.role_id = r.id
		where ur.user_id = #{id}
	</select>
</mapper>
```

마지막으로 UserDetailsService구현 클래스를 생성한다.  
```java
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private  UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		MyUser myUser = userDao.findById(userId);
		if(myUser == null) {
			throw new UsernameNotFoundException("사용자ID " + userId + "은(는) 존재하지 않습니다.");
		}
		return new User(myUser.getId(), myUser.getPassword(), getAuthorities(myUser));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(MyUser myUser){
		List<Role> roles = userDao.listRolesByUser(myUser);
		String[] userRoles = new String[roles.size()];
		for(int i= 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			userRoles[i] = role.getId(); 
		}
		Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
		return authorities;
	}
}
```

## 결과 테스트
브라우저에서 다음 주소를 호출하여 이것 저것 테스트 해 본다.    
http://localhost:8080/  

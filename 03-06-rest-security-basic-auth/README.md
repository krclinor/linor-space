# Spring Boot REST Basic Authentication

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 rest-mybatis를 복사하여 프로젝트를 생성한다.  

## 의존성 라이브러리 추가
Spring initializer로 Spring Security를 추가한다.  
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
	...
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
 	</dependencies>
```

## 사용자 및 권한관리를 위한 스키마 생성
소스 : [schema.sql](src/main/resources/schema.sql)
```sql
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
스프링 시큐리티는 사용자와 역할을 관리하기 위한 테이블이 필요하다.  
- USERS는 사용자정보 테이블로, ID, 이름, 이메일, 비밀번호를 관리한다.  
- ROLES는 권한 테이블이다.  
- USER_ROLE은 사용자에게 부여한 권한관리용 테이블이다.  

## 기초데이타 적제를 위한 데이타 스크립트
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
사용자 테이블에 admin, linor, user를 추가하여 테스트한다.  
비밀번호는 WebSecurityConfig에서 설정한 BCryptPasswordEncoder를 이용하여 인코딩한 비밀번호로 설정한다.  
비밀번호를 변경하려면 테스트 프로그램인 ApplicationsTest클래스에서 비밀번호를 수정하여 JUnit테스트를 실행하면 콘솔에 나타나도록 하였다.  
테스트케이스 [ApplicationTest.java](src/test/java/com/linor/singer/ApplicationTest.java)  
```java
	@Test
	public void testPassword() {
		log.info("linor -> 인코딩된 암호: " + encoder.encode("linor"));
		log.info("user -> 인코딩된 암호: " + encoder.encode("user"));
	}
```

## 보안 설정
소스 : [RestSecurityConfig.java](src/main/java/com/linor/singer/config/RestSecurityConfig.java)

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {
...
```
스프링프레임워크의 보안을 사용하기 위해 @EnaleWebSecurity를 선언하고, WebSecurityConfigurerAdapter를 상속받은 WebSecurityConfig클래스를 만든다.  

```java
	private final UserDetailsService userDetailsService;
	private final RestBasicAuthenticationEntryPoint authEntryPoint;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
```
로그인시 사용자 비번체크 및 권한 체크를 JDBC를 통해 처리하기 위해 userDetailsService를 선언한다.  
인증 실패시 401 HTTP상태코드 및 리턴할 정보의 JSON형식의 표현을 위해 아래에 추가로 생성할 RestBasicAuthenticationEntryPoint맴버변수를 선언한다.   
사용자 로그인시 사용자가 입력한 비밀번호를 인코딩하기 위해 passwordEncoder를 선언한다.    

```java
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			//CRSF Disable
			.csrf()
				.disable()
			//Basic Authentication
			.httpBasic()
				.authenticationEntryPoint(authEntryPoint)
				.and()
			//Stateless Session Management
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			//Authorization
			.authorizeRequests()
				.antMatchers("/rest/**")
					.hasAuthority("ADMIN")
				.anyRequest()
					.authenticated();
	}
```
REST API는 호출할 때마다 인증을 거치는 방식이므로 CRSF를 사용할 필요가 없으므로 disable로 설정한다.  
인증방식은 BASIC을 사용하고 인증실패시 처리할 접점을 선언한다.  
세션관리 정책은 STATELESS로 선언하여 상태관리를 하지 않도록 한다.  
권한설정에서는 /rest/로 시작하는 모든 URL에 대해서 ADMIN권한만 접속하도록 하고, 인증된 사용자만 접근하도록 한다.  

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
이미지, 자바스크립트, CSS등은 정적 컨텐츠로 호출시 마다 보안체크를 하면 성능에 문제가 발생할 수 있다.  
이를 방지하기 위해 이러한 페이지들은 보안처리를 거치지 않도록 한다.  

### BASIC인증 엔트리 포인트 구현
소스 : [RestBasicAuthenticationEntryPoint.java](src/main/java/com/linor/singer/security/RestBasicAuthenticationEntryPoint.java)
```java
@Component
public class RestBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,	authException.getMessage());
	}
}
```
인증 실패시 HTTP SC_UNAUTHORIZED(401)상태코드와 인증 실패 메시지를 리턴하도록 한다.  

## UserDetailsService 구현
로그인한 사용자의 인증과 권한체크를 위해서 UserDetailsService를 구현한다.  
이를 구현하기 위해 먼저 데이타베이스를 처리하기 위한 도메인 클래스, Mybatis인터페이스및 구현매퍼를 만든다.  

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

## 테스트용 html파일 생성
소스 : [test.html](src/main/resources/static/test.html)
```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>REST테스트</title>
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script> -->
<script src="/webjars/jquery/3.5.1/jquery.js"></script>
<script>
$(document).ready(function(){
	$("#user").click(function(){
		alert($("#username").val());
		base64Data =  btoa($("#username").val()+":"+$("#password").val());
		$.ajaxSetup({
			  headers: {
			    Authorization: "Basic " + base64Data
			  }
			});
	});
	
	$("#singer").click(function(){
		$.getJSON("/rest/singer", function(data, status){
			var strData = JSON.stringify(data);
			alert(strData);
		}).fail(function(data){
			alert(data.status);
			alert(data.responseJSON.message);
		});;
	});
});
</script>
</head>
<body>
	<input type="text" id="username"/><br/>
	<input type="password" id="password"/><br/>
	<button id="user">로그인 설정</button>
	<p/>
	<button id="singer">가져오기</button>
</body>
</html>
```
입력화면에서 사용자와 비밀번호를 입력하고 로그인 설정 버튼을 클릭하면 해더에 BASIC인증 정보를설정한다.  
가져오기 버튼을 클릭하면 /rest/singer REST API를 호출하여 가수목록을 가져와서 팝업창으로 화면에 표시한다.  

 
# Spring Boot REST JWT Authentication
JWT는 JSON Web Token의 약자로 프론트엔드 웹개발이 늘어나면서 많이 사용하는 인증방식이다.  
JWT구현방식은 다음과 같다.  
- 사용자명, 비밀번호를 JSON으로 인증하여 인증용 토큰과 갱신용토큰을 생성하여 클라이언트에 리턴한다.  
- 클라이언트는 헤더의 Authorization 값에 "Baerer {토큰값}"을 설정하여 REST URL을 호출한다.  
- 서버는 REST URL을 처리하기 전에 토큰값을 검증하여 사용자 인증처리를 수행한다.
- 인증에 성공하면 정상적으로 REST URL을 처리한다. 
- JWT토큰의 유효기간이 만료되면 갱신용 JWT를 이용하여 재인증한다. 

## Spring Boot Starter를 이용한 프로젝트 생성
이 프로젝트는 rest-mybatis를 복사하여 프로젝트를 생성한다.  

### 의존성 라이브러리 추가
Spring initializer로 Spring Security를 추가한다.  
소스 : [pom.xml](pom.xml)
```xml
	<dependencies>
	...
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>${jjwt.version}</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>${jjwt.version}</version>
		</dependency>
 	</dependencies>
```
JWT를 사용하기 위해 jjwt-impl와 jjwt-jackson을 추가한다.  

## 데이타베이스 처리
DBMS는 PostgreSql을 사용하며 ORM은 Mybatis를 이용하여 처리한다.  
### 데이타베이스 설정
[application.yml](src/main/resources/application.yml)  

```yml
#데이타소스
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://postgres:5432/spring?currentSchema=singer
    username: linor
    password: linor1234
    initialization-mode: always

mybatis:
  mapper-locations: classpath*:/**/dao/*.xml
  type-aliases-package: com.linor.singer.domain
  configuration.map-underscore-to-camel-case: true
```
데이타소스와 mybatis설정은 기존 rest-mybatis프로젝트와 동일하다.  

### 사용자 및 권한관리를 위한 스키마 생성
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

### 기초데이타 적제를 위한 데이타 스크립트
소스 : [data.sql](src/main/resources/data.sql)
```sql
insert into users(id, name, email, password, enabled, last_password_reset_date) values
('admin', '관리자', 'admin@gmail.com','$2a$10$i5PIsD4ak2IYV3aVhps4XuIF2bvKi54JjmItJ/qGz2ogVJjE/ycDy', true, now()),
('linor', '리노', 'linor@gmail.com', '$2a$10$Z6wZsEYowSJRTPoRNsIiRO68817rTLeOLlnwcnQ2LCHZKdsDgU65y', true, now()),
('user', '데모 사용자', 'user@gmail.com', '$2a$10$GmyV4/PqvfgcSHgTX6WYBeeY9STc.rvSTZAmYrMbqhW1dJZm6eLAe', true, now());

insert into roles(id, name) values
('ADMIN', '관리자 권한'),
('USER', '사용자 권한');

insert into user_role(user_id, role_id) values
('admin', 'ADMIN'),
('admin', 'USER'),
('linor', 'ADMIN'),
('linor', 'USER'),
('user', 'USER');
```
사용자, 권한를 테스트 하기 위해 기본 데이타를 테이블에 추가하는 데이타 스크립트이다.  
사용자 테이블에 admin, linor, user를 추가하여 테스트한다.  
비밀번호는 WebSecurityConfig에서 설정한 BCryptPasswordEncoder를 이용하여 인코딩한 비밀번호로 설정한다.  
비밀번호를 변경하려면 테스트 프로그램인 ApplicationsTest클래스에서 비밀번호를 수정하여 JUnit테스트를 실행하면 콘솔에 나타나도록 하였다.  

테스트케이스 [ApplicationTest.java](src/test/java/com/linor/ApplicationTest.java)  
```java
	@Test
	public void testPassword() {
		log.info("linor -> 인코딩된 암호: " + encoder.encode("linor"));
		log.info("user -> 인코딩된 암호: " + encoder.encode("user"));
	}
```
### DAO 구현
소스: [UserDao.java](src/main/java/com/linor/security/dao/UserDao.java)  
```java
@Mapper
@Repository
public interface UserDao {
	MyUser findById(String id);
	List<Role> listRolesByUser(MyUser user);
}
```
- findById: 사용자ID로 사용자 정보를 조회
- listRolesByUser: 사용자에 해당하는 권한목록을 조회

위 인터페이스 구현체인 Mapper를 생성한다.  
소스: [UserDao.xml](src/main/resources/com/linor/security/dao/UserDao.xml)  
```xml
<mapper namespace="com.linor.security.dao.UserDao">
	<select id="findById" resultType="com.linor.security.model.MyUser">
		select *
		from users
		where id = #{id}
	</select>
	<select id="listRolesByUser" resultType="com.linor.security.model.Role">
		select *
		from roles as r
		inner join user_role as ur
			on ur.role_id = r.id
		where ur.user_id = #{id}
	</select>
</mapper>
```

## Ajax로그인 인증 구현
jwt토큰을 이용한 인증을 하기 전에 사용자와 비밀번호를 받아서 인증처리를 해야 한다.  
인증에 성공하면 jwt토큰을 클라이언트에 전달한다.  
로그인 인증을 위해 다음 목록을 구현한다.  
1. AjaxLoginProcessingFilter  
2. AjaxAuthenticationProvider
3. AjaxAwareAuthenticationSuccessHandler
4. AjaxAwareAuthenticationFailureHandler
5. RestAuthenticationEntryPoint
6. WebSecurityConfig

### 처리 흐름 예
사용자는 인증 API를 호출한다.(/auth/login)  
다음은 curl로 호출하는 예이다.  
```bash
curl -X POST -H "X-Requested-With: XMLHttpRequest" -H "Content-Type: application/json" -H "Cache-Control: no-cache" -d '{
    "username": "linor",
    "password": "linor"
}' "http://localhost:8080/auth/login"
```

서버에서는 유효한 인증정보일 경우 다음과 같은 HTTP응답을 전달한다.  
- HTTP 200 OK 상태코드
- Response Body에 액세스용과 재발급용 JWT  
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdmxhZGFAZ21haWwuY29tIiwic2NvcGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1BSRU1JVU1fTUVNQkVSIl0sImlzcyI6Imh0dHA6Ly9zdmxhZGEuY29tIiwiaWF0IjoxNDcyMDMzMzA4LCJleHAiOjE0NzIwMzQyMDh9.41rxtplFRw55ffqcw1Fhy2pnxggssdWUU8CDOherC0Kw4sgt3-rw_mPSWSgQgsR0NLndFcMPh7LSQt5mkYqROQ",
  
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdmxhZGFAZ21haWwuY29tIiwic2NvcGVzIjpbIlJPTEVfUkVGUkVTSF9UT0tFTiJdLCJpc3MiOiJodHRwOi8vc3ZsYWRhLmNvbSIsImp0aSI6IjkwYWZlNzhjLTFkMmUtNDg2OS1hNzdlLTFkNzU0YjYwZTBjZSIsImlhdCI6MTQ3MjAzMzMwOCwiZXhwIjoxNDcyMDM2OTA4fQ.SEEG60YRznBB2O7Gn_5X6YbRmyB3ml4hnpSOxqkwQUFtqA6MZo7_n2Am2QhTJBJA1Ygv74F2IxiLv0urxGLQjg"
}
```

### JWT 액세스 토큰
JWT액세스 토큰은 인증과 권한설정에 사용된다.  
JWT액세스 토큰은 헤더(Header), 클레임(Claims), 서명(Signature) 3개 영역으로 나뉜다.  
헤더
```json
{
    "alg": "HS512"
}
```

클레임
```json
{
  "sub": "linor@gmail.com",
  "scopes": [
    "ROLE_ADMIN",
    "ROLE_PREMIUM_MEMBER"
  ],
  "iss": "http://linor.com",
  "iat": 1472033308,
  "exp": 1472034208
}
```

서명 (base64 encoded)
```
41rxtplFRw55ffqcw1Fhy2pnxggssdWUU8CDOherC0Kw4sgt3-rw_mPSWSgQgsR0NLndFcMPh7LSQt5mkYqROQ
```

### JWT 갱신용 토큰
갱신용 토큰은 액세스 토큰 만료시 재발급을 위한 용도로 사용되기 때문에 액세스 토큰에 비해 수명이 길어야 한다.  
회수되거나 문제가 있는 갱신용 토큰관리를 위해 클레임에 jti(JWT ID)를 추가한다.  
jti는 갱신용 토큰의 ID로 [RFC7519](https://tools.ietf.org/html/rfc7519#section-4.1.7)에 정의되어 있다.  

갱신용토큰을 해부하면 액세스 토큰과 마찬가지로 헤더(Header), 클레임(Claims), 서명(Signature) 3개 영역으로 나뉜다.  
헤더
```json
{
    "alg": "HS512"
}
```

클레임
```json
{
  "sub": "linor@gmail.com",
  "scopes": [
    "ROLE_ADMIN",
    "ROLE_PREMIUM_MEMBER"
  ],
  "iss": "http://linor.com",
  "jti": "90afe78c-1d2e-4869-a77e-1d754b60e0ce",
  "iat": 1472033308,
  "exp": 1472034208
}
```

서명 (base64 encoded)
```
41rxtplFRw55ffqcw1Fhy2pnxggssdWUU8CDOherC0Kw4sgt3-rw_mPSWSgQgsR0NLndFcMPh7LSQt5mkYqROQ
```

### AjaxLoginProcessingFilter
소스: [AjaxLoginProcessingFilter.java](src/main/java/com/linor/security/auth/ajax/AjaxLoginProcessingFilter.java)
첫 단계로 Ajax인증 요청을 처리하기 위해 AbstractAuthenticationProcessingFilter를 상속받아 구현한다.  
```java
@Slf4j
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final AuthenticationSuccessHandler successHandler;
	private final AuthenticationFailureHandler failureHandler;

	private final ObjectMapper objectMapper;

	public AjaxLoginProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
			AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
		super(defaultProcessUrl);
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.objectMapper = mapper;
	}
...
```

attemptAuthentication메서드에서 JSON타입의 인증내용이 유효한지 검사한다. 
```java
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		if (!HttpMethod.POST.name().equals(request.getMethod()) || !WebUtil.isAjax(request)) {
			if (log.isDebugEnabled()) {
				log.debug("Authentication method not supported. Request method: " + request.getMethod());
			}
			throw new AuthMethodNotSupportedException("Authentication method not supported");
		}

		LoginForm loginRequest = objectMapper.readValue(request.getReader(), LoginForm.class);

		if (StringUtils.isEmpty(loginRequest.getUsername()) || StringUtils.isEmpty(loginRequest.getPassword())) {
			throw new AuthenticationServiceException("Username or Password not provided");
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
				loginRequest.getPassword());

		return this.getAuthenticationManager().authenticate(token);
	}
```
인증정보에대한 유효성 검증 세부 처리 로직은 아래에 설명할 AjaxAuthenticationProvider클래스가 처리한다.  

인증성공시 successfulAuthentication메서드가 호출된다.
```java
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		successHandler.onAuthenticationSuccess(request, response, authResult);
	}
```

인증실패시 unsuccessfulAuthentication메서드가 호출된다.  
```java
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		failureHandler.onAuthenticationFailure(request, response, failed);
	}
```

### AjaxAuthenticationProvider
소스: [AjaxAuthenticationProvider.java](src/main/java/com/linor/security/auth/ajax/AjaxAuthenticationProvider.java)  
```java
@Component
@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {
    private final BCryptPasswordEncoder encoder;
    private final DatabaseUserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        MyUser user = userService.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
        
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getId()))
                .collect(Collectors.toList());
        
        UserContext userContext = UserContext.create(user.getId(), authorities);
        
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
```
AjaxAuthenticationProvider클래스가 할 일은 다음과 같다.  
1. 사용자의 인증정보가 데이타베이스 또는 LDAP등에 저장된 정보와 비교하여 유효한지 체크한다.  
2. username과 password가 데이타베이스의 인증정보와 일치하지 않을 경우 예외처리한다.  
3. 인증성공시 UserContext객체를 생성하고 이 객체에 사용자명, 권한목록을 입력한다.  
4. JWT토큰을 생성하기 위해 AjaxAwareAuthenticationSuccessHandler로 제어를 넘긴다.    

JWT 액세스 토큰과 갱신용 토큰을 생성하기 위해 [Java JWT](https://github.com/jwtk/jjwt)라이브러리를 사용한다. 
토큰 생성로직에 대한 라이브러리의 결합도를 낮추기 위해 JwtTokenFactory팩토리 클래스를 만든다.  
이렇게 하면 추후에 라이브러리를 교체하면 이 팩토리 클래스만 수정하면 된다.  
소스: [JwtTokenFactory.java](src/main/java/com/linor/security/model/token/JwtTokenFactory.java)  
```java
@Component
@RequiredArgsConstructor
public class JwtTokenFactory {
    private final JwtSettings settings;

    public AccessJwtToken createAccessJwtToken(UserContext userContext) {
        if (StringUtils.isEmpty(userContext.getUsername())) 
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) 
            throw new IllegalArgumentException("User doesn't have any privileges");

        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put("scopes", userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));

        LocalDateTime currentTime = LocalDateTime.now();
        
        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(settings.getTokenIssuer())
          .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
          .setExpiration(Date.from(currentTime
              .plusMinutes(settings.getTokenExpirationTime())
              .atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(settings.getKey(), SignatureAlgorithm.HS512)
        .compact();

        return new AccessJwtToken(token, claims);
    }

    public JwtToken createRefreshToken(UserContext userContext) {
        if (StringUtils.isEmpty(userContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));
        
        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(settings.getTokenIssuer())
          .setId(UUID.randomUUID().toString())
          .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
          .setExpiration(Date.from(currentTime
              .plusMinutes(settings.getRefreshTokenExpTime())
              .atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(settings.getKey(), SignatureAlgorithm.HS512)
        .compact();

        return new AccessJwtToken(token, claims);
    }
}
```
createAccessJwtToken메서드는 JWT액세스 토큰을 생성한다.
createRefreshToken메서드는 JWT갱신용 토큰을 생성한다.  

### AjaxAwareAuthenticationFailureHandler
인증 실패시 AjaxLoginProcessingFilter가 호출하도록 AjaxAwareAuthenticationFailureHandler클래스를 생성한다.  
소스: [AjaxAwareAuthenticationFailureHandler.java](src/main/java/com/linor/security/auth/ajax/AjaxAwareAuthenticationFailureHandler.java)  
```java
@Component
@RequiredArgsConstructor
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private final ObjectMapper mapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if (e instanceof BadCredentialsException) {
			mapper.writeValue(response.getWriter(), ErrorResponse.of("Invalid username or password",
					ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		} else if (e instanceof JwtExpiredTokenException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of("Token has expired", ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
		} else if (e instanceof AuthMethodNotSupportedException) {
			mapper.writeValue(response.getWriter(),
					ErrorResponse.of(e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
		}

		mapper.writeValue(response.getWriter(),
				ErrorResponse.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
	}
}
```
인증처리시 발생할 수 있는 예외 유형에 따라 에러메시지를 지정한다.  

## JWT 인증 처리
토큰기반 인증은 세션/쿠키에 비해 다음과 같은 장단점이 있다.  
장점
1. CORS
2. CSRF보호가 필요 없음
3. 개선된 모바일 통합
4. 권한서버의 부하 감소
5. 분산된 세션저장소가 필요 없음

단점
1. XSS공격에 더 취약함
2. 액세스 토큰이 만료된 권한 클레임을 가질 수 있음
3. 클레임의 내용이 많아지면 액세스토큰의 사이즈가 커질 수 있음
4. 파일다운로드 API구현의 까다로움
5. 완전한 무상태와 취소는 상호 배타적임

JWT인증 흐름은 매우 단순하다.
1. 사용자는 권한서버가 제공하는 갱신용과 액세스용 토큰을 획득한다.  
2. 사용자는 보호API자원을 요청하기 위해 액세스토큰을 첨부한다.  
3. 액세스토큰은 사용자ID와 권한 클레임(claim)을 식별하는데 사용된다.  

여기서 주의할 점은 액세스 토큰에 권한 클레임이 포함되어 있다는 것이다.  
액세스 토큰의 생명주기동안에 사용자의 권한이 변경될 경우 액세스 토큰에 있는 권한과 실제권한이 불일치하는 경우가 발생할 수 있다.  
하지만 대부분의 경우 액세스 토큰의 생명주기가 짧기 때문에 큰 문제가 되지는 않는다.  

### 보호API 요청 예
액세스 토큰을 해더에 포함한다.
```
<header-name> Bearer <access_token>
``` 
우리는 헤더명(<header-name>)을 Authorization으로 사용한다.  
CURL:  
```bash
curl -X GET -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdmxhZGFAZ21haWwuY29tIiwic2NvcGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1BSRU1JVU1fTUVNQkVSIl0sImlzcyI6Imh0dHA6Ly9zdmxhZGEuY29tIiwiaWF0IjoxNDcyMzkwMDY1LCJleHAiOjE0NzIzOTA5NjV9.Y9BR7q3f1npsSEYubz-u8tQ8dDOdBcVPFN7AIfWwO37KyhRugVzEbWVPO1obQlHNJWA0Nx1KrEqHqMEjuNWo5w" -H "Cache-Control: no-cache" "http://localhost:8080/rest/me"
```

JWT 인증 구현을 위해 다음 컴포넌트를 생성한다.  
1. JwtTokenAuthenticationProcessingFilter
2. JwtAuthenticationProvider
3. SkipPathRequestMatcher
4. JwtHeaderTokenExtractor
5. BloomFilterTokenVerifier
6. WebSecurityConfig

### JwtTokenAuthenticationProcessingFilter
소스: [JwtTokenAuthenticationProcessingFilter.java](src/main/java/com/linor/security/auth/jwt/JwtTokenAuthenticationProcessingFilter.java)  

JwtTokenAuthenticationProcessingFilter는 '/rest/**'에 해당하는 모든 API에 적용된다. 
갱신용 토큰으로 액세스토큰을 갱신하기 위한 '/auth/token'와 로그인을 위한 '/auth/login'에는 적용하지 않는다.  

이 필터는 다음 작업을 처리한다. 
1. 헤더의 'Authorization'항목의 액세스 토큰을 검사한다. 헤더에 엑세스 토큰값이 발견되면 JwtAuthenticationProvider로 제어를 넘겨 인증을 진행하도록 하고, 토큰값이 없으면 권한 예외상황을 발생한다.  
2. JwtAuthenticationProvider에서 처리된 인증 결과에 따라 성공 또는 실패관련 메서드를 호출한다.  
```java
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationFailureHandler failureHandler;
    
    @Autowired
    public JwtTokenAuthenticationProcessingFilter(AuthenticationFailureHandler failureHandler, 
            RequestMatcher matcher) {
        super(matcher);
        this.failureHandler = failureHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String tokenPayload = request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME);
        RawAccessJwtToken token = new RawAccessJwtToken(WebUtil.tokenExtract(tokenPayload));
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
```
인증 성공시 chain.doFilter(request, response)를 호출해야 한다. 

### JwtHeaderTokenExtractor
JwtHeaderTokenExtractor컴포넌트는 요청 헤더에서 JWT토큰을 추출하는 클래스이다.  
소스: [JwtHeaderTokenExtractor.java](src/main/java/com/linor/security/auth/jwt/JwtHeaderTokenExtractor.java)  
```java
@Component
public class JwtHeaderTokenExtractor implements TokenExtractor {
	public static String HEADER_PREFIX = "Bearer ";

	@Override
	public String extract(String header) {
		if (StringUtils.isEmpty(header)) {
			throw new AuthenticationServiceException("Authorization header cannot be blank!");
		}
		if (header.length() < HEADER_PREFIX.length()) {
			throw new AuthenticationServiceException("Invalid authorization header size.");
		}
		return header.substring(HEADER_PREFIX.length(), header.length());
	}
}
```

### JwtAuthenticationProvider
소스: [JwtAuthenticationProvider.java](src/main/java/com/linor/security/auth/jwt/JwtAuthenticationProvider.java)  
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtSettings jwtSettings;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getKey());
        String subject = jwsClaims.getBody().getSubject();
        List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
        List<GrantedAuthority> authorities = scopes.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        UserContext context = UserContext.create(subject, authorities);
        
        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
```
JwtAuthenticationProvider는 다음 작업을 수행한다.  
1. 액세스 토큰값 검증
2. 액세스 토큰에서 사용자ID와 권한클래임을 추출하여 UserContext객체 생성
3. 액세서 토큰값이 잘못된 경우 적절한 인증 예외상황 발생

### SkipPathRequestMatcher
JwtTokenAuthenticationProcessingFilter필더는 '/auth/login'과 '/auth/token'API를 제외하도록 설정해야 한다.  
이를 수행하기 위해 RequestMatcher인터페이스 구현체로 SkipPathRequestMatcher클래스를 생성한다.  
소스: [SkipPathRequestMatcher.java](src/main/java/com/linor/security/auth/jwt/SkipPathRequestMatcher.java)  
```java
public class SkipPathRequestMatcher implements RequestMatcher {
	private OrRequestMatcher matchers;
	private RequestMatcher processingMatcher;

	public SkipPathRequestMatcher(List<String> pathsToSkip, String processingPath) {
		Assert.notNull(pathsToSkip, "This argument is required; it must not be null");
		List<RequestMatcher> m = pathsToSkip.stream().map(path -> new AntPathRequestMatcher(path))
				.collect(Collectors.toList());
		matchers = new OrRequestMatcher(m);
		processingMatcher = new AntPathRequestMatcher(processingPath);
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (matchers.matches(request)) {
			return false;
		}
		return processingMatcher.matches(request) ? true : false;
	}
}
```

### WebSecurityConfig
보안설정을 위해 WebSecurityConfigurerAdapter를 상속받은 WebSecurityConfig를 생성한다.  
소스: [WebSecurityConfig.java](src/main/java/com/linor/security/config/WebSecurityConfig.java)  
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
	private static final String AUTHENTICATION_URL = "/auth/login";
	private static final String REFRESH_TOKEN_URL = "/auth/token";
	private static final String API_ROOT_URL = "/rest/**";

	@Autowired private RestAuthenticationEntryPoint authenticationEntryPoint;
	@Autowired private AuthenticationSuccessHandler successHandler;
	@Autowired private AuthenticationFailureHandler failureHandler;
	@Autowired private AjaxAuthenticationProvider ajaxAuthenticationProvider;
	@Autowired private JwtAuthenticationProvider jwtAuthenticationProvider;
	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private TokenExtractor tokenExtractor;
	@Autowired private ObjectMapper objectMapper;

	protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(String loginEntryPoint) throws Exception {
		AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(loginEntryPoint, successHandler,
				failureHandler, objectMapper);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}

	protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(
			List<String> pathsToSkip, String pattern) throws Exception {
		SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
		JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
		filter.setAuthenticationManager(this.authenticationManager);
		return filter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(ajaxAuthenticationProvider);
		auth.authenticationProvider(jwtAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		List<String> permitAllEndpointList = Arrays.asList(AUTHENTICATION_URL, REFRESH_TOKEN_URL, "/console");

		http.csrf().disable() // We don't need CSRF for JWT based authentication
				.exceptionHandling().authenticationEntryPoint(this.authenticationEntryPoint)

				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
					.authorizeRequests()
				.antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()]))
					.permitAll()
				.anyRequest()
					.authenticated() // Protected API End-points

				.and()
				.addFilterBefore(buildAjaxLoginProcessingFilter(AUTHENTICATION_URL),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList, API_ROOT_URL),
						UsernamePasswordAuthenticationFilter.class);
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

이 클래스는 다음과 같은 구성과 인스턴스를 생성한다.  
1. AjaxLoginProcessingFilter
2. JwtTokenAuthenticationProcessingFilter
3. AuthenticationManager
4. BCryptPasswordEncoder

configure(HttpSecurity http)메서드는 보호/비보호 API를 정의하기 위해 패턴을 구성한다. 
쿠키를 사용하지 않기 때문에 CSRF보호를 해제한다.  

### PasswordEncoderConfig
비밀번호 암호화를 위해 BCryp인코더 빈을 설정한다.
소스: [PasswordEncoderConfig.java](src/main/java/com/linor/security/config/PasswordEncoderConfig.java)  
```java
@Configuration
public class PasswordEncoderConfig {
	@Bean
	protected BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
```
passwordEncoder빈은 보통 WebSecurityConfig내에 선언하여 사용하는에 여기에서는 Bean순환참조가 발생하여 별도클래스로 생성하였다.  

### BloomFilterTokenVerifier
이 클래스는 더미클래스로 토큰값 검증을 위해 필요한 경우 구현하면 된다.  
소스: [BloomFilterTokenVerifier.java](src/main/java/com/linor/security/auth/jwt/BloomFilterTokenVerifier.java)  
```java
@Component
public class BloomFilterTokenVerifier implements TokenVerifier {
	@Override
	public boolean verify(String jti) {
		return true;
	}
}
```

##결론
JWT토큰을 일어버리는 것은 집키를 일어버리는 것과 같다고들 한다. 따라서 주의해서 사용해야 한다.  

## 참고사이트
https://svlada.com/jwt-token-authentication-with-spring-boot/
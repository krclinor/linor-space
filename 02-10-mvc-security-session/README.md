# Spring Boot 보안 동일 유저 세션수 제한
동일한 사용자ID로 여러 곳에서 접속하는 것을 막을 필요가 있을 경우 사용한다.  
이 프로젝트는 mvc-security-mybatis를 복사하여 진행한다.  

모든 설정 및 구현은 mvc-security-mybatis프로젝트와 동일하다.

## 페이지별 보안 설정 수정
소스 : [WebSecurityConfig.java](src/main/java/com/linor/singer/config/WebSecurityConfig.java)
```java
	protected void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()
				.antMatchers("/", "/logoutSuccess")
					.permitAll()
				.antMatchers("/admin/")
					.hasAuthority("ADMIN")
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
		//유저당 세션관리
		http.sessionManagement()
			.maximumSessions(1)	//유저당 최대 세션수
			.maxSessionsPreventsLogin(false)//최대 세션수 초과시 접속 불가처리
			.expiredUrl("/sessionExpired.html");
	}
```
http.sessionManagement를 추가한다.
- maximumSessions : Session허용 개수를 지정한다.
- maxSessionsPreventsLogin : true이면 동일사용자가 최대 세션수 만큼 로그인한 경우 login할 수 없다. false이면 login할 수 있고, 기존Session이 종료된다. 디폴트는 false이다.  

## 결과 테스트
브라우저에서 다음 주소를 호출하여 테스트 해 본다.  
동일브라우저에서는 세션값이 공유되어 테스트 할 수 없고, 다른 브라우저를 사용해야 한다.  
http://localhost:8080/  

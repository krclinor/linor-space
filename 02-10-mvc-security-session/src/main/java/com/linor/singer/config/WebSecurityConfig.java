package com.linor.singer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * 동시사용 세션 제어를 위한 설정
	 * @return
	 */
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
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
			.maxSessionsPreventsLogin(true)//최대 세션수 초과시 접속 불가처리
			.expiredUrl("/sessionExpired.html");
		
		//Basic인증 사용
		http.httpBasic();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(
					HttpMethod.GET, 
					"/", "/favicon.ico", "/*.html", "/**/*.html",
					"/webjars/**", "/css/**", "/fonts/**", "/js/**", "/images/**");
	}
}

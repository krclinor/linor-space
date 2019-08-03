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
					.hasAuthority("ADMIN")
//				.antMatchers("/user/**")
//					.access("@webSecurity.check(authentication, request)")
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

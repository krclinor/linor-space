package com.linor.singer.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorAware")
public class JpaAuditConfig {
	@Bean
	public AuditorAware<String> auditorAware(){
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of("Linor");
			}
		};
	}

/*	
	// Spring Security 사용시 적용
	@Bean
	public AuditorAware<User> auditorAware(){
		return new AuditorAware<User>() {
			@Override
			public Optional<User> getCurrentAuditor() {
				  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				  if (authentication == null || !authentication.isAuthenticated()) {
				   return null;
				  }
				  return Optional.of((User) authentication.getPrincipal());
			}
		};
	}
*/
}

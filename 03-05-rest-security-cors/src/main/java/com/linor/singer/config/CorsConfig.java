package com.linor.singer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("DEV")	//개발시에만 사용하도록 한 경우
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins("http://localhost:3000", "http://domain2.com")
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowCredentials(false)
			.maxAge(3600);
	}
	
}

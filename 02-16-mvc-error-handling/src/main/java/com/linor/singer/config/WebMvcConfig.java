package com.linor.singer.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		mappings.setProperty("DataAccessException", "dbError");
		mappings.setProperty("RuntimeException", "error");
		
		exceptionResolver.setExceptionMappings(mappings);
		exceptionResolver.setDefaultErrorView("error");
		return exceptionResolver;
	}
}

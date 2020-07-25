package com.linor.singer.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

//@Configuration
//@EnableWebMvc
//public class WebMvcConfig implements WebMvcConfigurer {
//	@Bean
//	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
//		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
//		Properties mappings = new Properties();
//		mappings.setProperty("DataAccessException", "dbError");
//		mappings.setProperty("BizException", "bizError");
//		mappings.setProperty("RuntimeException", "runError");
//		mappings.setProperty("ResourceNotFoundException", "404Error");
//		
//		exceptionResolver.setExceptionMappings(mappings);
//		exceptionResolver.setDefaultErrorView("customError");
//		return exceptionResolver;
//	}
//	
//    @Bean
//    public ViewResolver viewResolver () {
//        InternalResourceViewResolver viewResolver =
//                  new InternalResourceViewResolver();
//        viewResolver.setPrefix("/WEB-INF/jsp/");
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }
//}

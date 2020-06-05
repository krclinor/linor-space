package com.linor.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.linor.app.component.RequestLogRegistFilter;

@Configuration
public class ControllersConfig  implements WebMvcConfigurer{
//	@Override
//	public void configureViewResolvers(ViewResolverRegistry registry) {
//		registry.jsp("/WEB-INF/jsp/", ".jsp");
//	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/hello");
	}
	
	@Bean
	public FilterRegistrationBean<RequestLogRegistFilter> loggingFilter(){
		FilterRegistrationBean<RequestLogRegistFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLogRegistFilter());
		registrationBean.addUrlPatterns("/user/*");
		return registrationBean;
	}
}

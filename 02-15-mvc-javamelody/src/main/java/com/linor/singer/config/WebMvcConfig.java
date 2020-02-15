package com.linor.singer.config;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean(name = "javamelodyFilter")
	public FilterRegistrationBean<MonitoringFilter> javamelodyFilterBean(){
		FilterRegistrationBean<MonitoringFilter> registration =
				new FilterRegistrationBean<MonitoringFilter>();
		registration.setFilter(new MonitoringFilter());
		registration.addUrlPatterns("/*");
		registration.setName("javamelodyFilter");
		registration.setAsyncSupported(true);
		registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
		
		return registration;
	}
	
	@Bean(name = "javamelodySessionListener")
	public ServletListenerRegistrationBean<SessionListener> sessionListener(){
		return new ServletListenerRegistrationBean<SessionListener>(new SessionListener());
	}
}

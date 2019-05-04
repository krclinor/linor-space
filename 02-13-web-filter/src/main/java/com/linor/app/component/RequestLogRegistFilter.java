package com.linor.app.component;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLogRegistFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		long startMilis = System.currentTimeMillis();

		log.info("User Logging Request{}: {} - {} ", startMilis, req.getMethod(), req.getRequestURI());

		chain.doFilter(request, response);

		log.info("User Loggin Response {} : {}", startMilis, response.getContentType());
		log.info("User Duration {} : {}", startMilis, System.currentTimeMillis() - startMilis );
	}

}

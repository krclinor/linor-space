package com.linor.singer.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linor.singer.domain.ErrorDetail;

@Component
public class RestBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper mapper;
	
	@Autowired
	public RestBasicAuthenticationEntryPoint(MappingJackson2HttpMessageConverter messageConverter) {
		this.mapper = messageConverter.getObjectMapper();
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.UNAUTHORIZED.value());
		errorDetail.setErrorMessage("인증에 실패하였습니다.");
		errorDetail.setDevErrorMessage(getStackTraceAsString(authException));
		
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, errorDetail);
		writer.flush();
		writer.close();
	}

	private String getStackTraceAsString(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}

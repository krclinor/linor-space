package com.linor.singer.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.linor.singer.exception.BizException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BizException.class)
	public String handleBizException(HttpServletRequest req, BizException bizEx) {
		log.info("BizException Occurred:: URL=" + req.getRequestURL());
		return "biz_error";
	}
	
	@ExceptionHandler(ServletRequestBindingException.class)
	public String servletRequestBindingException(ServletRequestBindingException ex) {
		log.error("ServletRequestBindingException occurred: " + ex.getMessage());
		return "validation_error";
	}
}

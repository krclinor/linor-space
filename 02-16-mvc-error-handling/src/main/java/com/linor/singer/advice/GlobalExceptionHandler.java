package com.linor.singer.advice;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.linor.singer.exception.BizException;
import com.linor.singer.exception.DataAccessException;
import com.linor.singer.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DataAccessException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public String handleDataAccessException(DataAccessException e, Model model) {
		model.addAttribute("exception", e);
		return "dbError";
	}

	@ExceptionHandler(BizException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public String handleBizException(BizException e, Model model) {
		model.addAttribute("exception", e);
		return "bizError";
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleRuntimeException(RuntimeException e, Model model) {
		model.addAttribute("exception", e);
		return "runError";
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleResourceNotFoundException(ResourceNotFoundException e, Model model) {
		model.addAttribute("exception", e);
		return "404Error";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleException(Exception e, Model model) {
		model.addAttribute("exception", e);
		return "customError";
	}
}

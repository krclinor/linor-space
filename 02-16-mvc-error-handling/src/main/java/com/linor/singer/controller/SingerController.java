package com.linor.singer.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.linor.singer.exception.BizException;
import com.linor.singer.exception.DataAccessException;
import com.linor.singer.exception.ResourceNotFoundException;

@Controller
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	@RequestMapping("/")
	public String welcom(Model model) {
		if(message == null)
			throw new ResourceNotFoundException();
		
		model.addAttribute("message", this.message);
		return "welcome";
	}

	@RequestMapping("/res")
	public String resProc(Model model) throws Exception{
		if(model.getAttribute("aa") == null)
		throw new ResourceNotFoundException();
		
		return "welcome";
	}
	@RequestMapping("/db")
	public String dbProc(Model model) throws Exception{
		if(model.getAttribute("aa") == null)
		throw new DataAccessException("알수없는 데이타베이스 오류 발생");
		
		return "welcome";
	}

	@RequestMapping("/biz")
	public String bizProc(Model model) {
		if(model.getAttribute("aa") == null)
		throw new BizException("비즈니스 오류 발생");
		return "welcome";
	}

	@RequestMapping("/run")
	public String run(Model model) {
		if(model.getAttribute("aa") == null)
		throw new RuntimeException("런타임 오류 발생");
		return "welcome";
	}

	@RequestMapping("/io")
	public String error(Model model) throws Exception{
		if(model.getAttribute("aa") == null)
		throw new IOException("알 수 없는 오류 발생");
		return "welcome";
	}

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

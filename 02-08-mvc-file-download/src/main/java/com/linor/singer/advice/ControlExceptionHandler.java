package com.linor.singer.advice;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ControlExceptionHandler {
	
	@ExceptionHandler(value= FileNotFoundException.class)
	public void handle(FileNotFoundException ex, HttpServletResponse response) throws IOException {
		log.error("오류 발생(파일이 존재하지 않음) --->>>");
		log.error(ex.getMessage());
		response.sendError(404, ex.getMessage());
	}
	
	@ExceptionHandler(value=IOException.class)
	public void handle(IOException ex, HttpServletResponse response) throws IOException {
		log.error("IO예외상황 발생 --->>>");
		log.error(ex.getMessage());
		response.sendError(500, ex.getMessage());
	}
}

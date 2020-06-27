package com.linor.singer.advice;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.linor.singer.domain.ErrorDetail;
import com.linor.singer.exception.BizException;
import com.linor.singer.exception.DataAccessException;
import com.linor.singer.exception.ResourceNotFoundException;

@ControllerAdvice
public class ControllExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.NOT_FOUND.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BizException.class)
	public ResponseEntity<?> bizException(BizException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.SERVICE_UNAVAILABLE.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<?> dataAccessException(DataAccessException e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private String getStackTraceAsString(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}

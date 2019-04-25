package com.linor.singer.advice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.linor.singer.domain.ErrorDetail;
import com.linor.singer.domain.FieldErrorMessage;
import com.linor.singer.exception.ResourceNotFoundException;
import com.linor.singer.exception.ValidException;

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
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exception(Exception e){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		errorDetail.setErrorMessage(e.getMessage());
		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		return new ResponseEntity<>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ValidException.class)
	public ResponseEntity<?> validException(ValidException e, WebRequest request){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		errorDetail.setErrorMessage(e.getMessage());

		List<FieldErrorMessage> fieldErrorMessages = new ArrayList<>();
		e.getErrors().getFieldErrors().forEach(fieldError -> {
			FieldErrorMessage fieldErrorMessage = FieldErrorMessage.builder()
					.resource(fieldError.getObjectName())
					.field(fieldError.getField())
					.code(fieldError.getCode())
					.message(fieldError.getDefaultMessage())
					.build();
			fieldErrorMessages.add(fieldErrorMessage);
		});
		errorDetail.setFieldErrors(fieldErrorMessages);

		errorDetail.setDevErrorMessage(getStackTraceAsString(e));
		
		return new ResponseEntity<>(errorDetail, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	private String getStackTraceAsString(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}

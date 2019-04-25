package com.linor.singer.exception;

import org.springframework.validation.Errors;

import lombok.Getter;

@Getter
public class ValidException extends RuntimeException{
	private Errors errors;
	
	public ValidException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}
	
}

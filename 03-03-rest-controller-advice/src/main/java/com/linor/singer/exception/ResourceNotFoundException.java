package com.linor.singer.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException() {
		this("해당 자원이 존재하지 않습니다.");
	}
	public ResourceNotFoundException(String message){
		this(message, null);
	}
	
	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

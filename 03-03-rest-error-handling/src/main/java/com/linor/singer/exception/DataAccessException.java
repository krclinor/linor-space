package com.linor.singer.exception;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataAccessException extends SQLException {
	
	public DataAccessException() {
		this("알수 없는 오류가 발생했습니다.");
	}
	public DataAccessException(String message){
		this(message, null);
	}
	
	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}

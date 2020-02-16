package com.linor.singer.exception;

public class BizException extends RuntimeException {
	
	public BizException() {
		this("알수 없는 오류가 발생했습니다.");
	}
	public BizException(String message){
		this(message, null);
	}
	
	public BizException(String message, Throwable cause) {
		super(message, cause);
	}
}

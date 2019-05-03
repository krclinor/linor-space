package com.linor.app.exception;

public class BizException extends RuntimeException {
	public BizException() {
		this("시스템 처리시 오류가 발생하였습니다.\n 관리자에게 문의하시기 바랍니다.");
	}
	public BizException(String message){
		this(message, null);
	}
	
	public BizException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

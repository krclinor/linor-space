package com.linor.security.model;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {
	// HTTP Response Status Code
	private final HttpStatus status;

	// General Error message
	private final String message;

	// Error code
	private final ErrorCode errorCode;

	private final Date timestamp;

	protected ErrorResponse(final String message, final ErrorCode errorCode, HttpStatus status) {
		this.message = message;
		this.errorCode = errorCode;
		this.status = status;
		this.timestamp = new java.util.Date();
	}

	public static ErrorResponse of(final String message, final ErrorCode errorCode, HttpStatus status) {
		return new ErrorResponse(message, errorCode, status);
	}

}

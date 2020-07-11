package com.linor.security.exceptions;

public class InvalidJwtToken extends RuntimeException {
	private static final long serialVersionUID = -294671188037098603L;

	public InvalidJwtToken(String message) {
		super(message);
	}

}

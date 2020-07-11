package com.linor.security.exceptions;

import org.springframework.security.authentication.AuthenticationServiceException;

public class AuthMethodNotSupportedException extends AuthenticationServiceException {
	private static final long serialVersionUID = 6497733516195793476L;

	public AuthMethodNotSupportedException(String msg) {
		super(msg);
	}
}

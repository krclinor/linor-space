package com.linor.security.exceptions;

import org.springframework.security.core.AuthenticationException;

import com.linor.security.model.token.JwtToken;

public class JwtExpiredTokenException extends AuthenticationException {
	private static final long serialVersionUID = -2852981151721908807L;
	private JwtToken token;

	public JwtExpiredTokenException(String msg) {
		super(msg);
	}

	public JwtExpiredTokenException(JwtToken token, String msg, Throwable t) {
		super(msg, t);
		this.token = token;
	}

	public String token() {
		return this.token.getToken();
	}
}

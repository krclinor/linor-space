package com.linor.security.auth.jwt;

public interface TokenVerifier {
	public boolean verify(String jti);
}

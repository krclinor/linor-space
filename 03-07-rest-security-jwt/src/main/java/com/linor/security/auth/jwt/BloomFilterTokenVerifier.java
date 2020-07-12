package com.linor.security.auth.jwt;

import org.springframework.stereotype.Component;

@Component
public class BloomFilterTokenVerifier implements TokenVerifier {
	@Override
	public boolean verify(String jti) {
		return true;
	}
}
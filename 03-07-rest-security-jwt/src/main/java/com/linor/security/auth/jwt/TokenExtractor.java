package com.linor.security.auth.jwt;

public interface TokenExtractor {
	public String extract(String header);
}

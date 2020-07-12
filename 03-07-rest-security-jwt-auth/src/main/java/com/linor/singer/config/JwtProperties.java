package com.linor.singer.config;

import java.security.Key;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String issure;
	private int expiresIn;
	private int refreshExpiresIn;
	private String secret;
	private Key key;
	
	public void setSecret(String secret) {
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}
}

package com.linor.security.config;

import java.security.Key;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtSettings {
	private Integer tokenExpirationTime;
	private String tokenIssuer;
	private String tokenSigningKey;
	private Integer refreshTokenExpTime;

	private Key key;

	public void setTokenSigningKey(String tokenSigningKey) {
		this.tokenSigningKey = tokenSigningKey;
		this.key = Keys.hmacShaKeyFor(tokenSigningKey.getBytes());
	}
}

package com.linor.singer.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String appName;
	private String secret;
	private int expiresIn;
	private String authHeader;
	private String headerPrefix;
}

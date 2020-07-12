package com.linor.singer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenState {
	private String accessToken;
	private long expiresIn;
	private String refreshToken;
	private long refreshExpiresIn;
	
	public UserTokenState(String accessToken, long expiresIn) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
	}
}

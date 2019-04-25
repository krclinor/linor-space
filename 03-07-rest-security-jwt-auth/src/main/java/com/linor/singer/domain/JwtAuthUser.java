package com.linor.singer.domain;

import lombok.Data;

@Data
public class JwtAuthUser {
	private String username;
	private String password;
}

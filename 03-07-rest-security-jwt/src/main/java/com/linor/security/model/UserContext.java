package com.linor.security.model;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserContext {
	private final String username;
	private final List<GrantedAuthority> authorities;

	public static UserContext create(String username, List<GrantedAuthority> authorities) {
		if (StringUtils.isEmpty(username))
			throw new IllegalArgumentException("Username is blank: " + username);
		return new UserContext(username, authorities);
	}
}

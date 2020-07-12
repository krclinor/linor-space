package com.linor.singer.domain;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

public class JwtAuthentication extends AbstractAuthenticationToken{
	private static final long serialVersionUID = -5167176415639270000L;

	@Getter
	@Setter
	private String token;
	private final UserDetails principle;
	
	public JwtAuthentication(UserDetails principle) {
		super(principle.getAuthorities());
		this.principle = principle;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public UserDetails getPrincipal() {
		return principle;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}
	
}

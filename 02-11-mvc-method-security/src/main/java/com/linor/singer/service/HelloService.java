package com.linor.singer.service;

import org.springframework.security.access.prepost.PreAuthorize;

public interface HelloService {
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String getHelloMessage();
}

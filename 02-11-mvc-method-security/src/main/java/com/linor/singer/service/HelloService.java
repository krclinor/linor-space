package com.linor.singer.service;

import org.springframework.security.access.prepost.PreAuthorize;

public interface HelloService {
	@PreAuthorize("hasAuthority('ADMIN')")
	public String getHelloMessage();
}

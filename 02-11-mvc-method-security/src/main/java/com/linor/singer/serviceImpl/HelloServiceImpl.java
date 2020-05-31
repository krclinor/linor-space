package com.linor.singer.serviceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.linor.singer.service.HelloService;

@Service
public class HelloServiceImpl implements HelloService {
	@Override
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String getHelloMessage() {
		return "안녕하세요!!";
	}
}

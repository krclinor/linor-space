package com.linor.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public String sayHello1() {
		return "Hello World!";
	}

	@GetMapping("/user/hello")
	public String sayHello2() {
		return "Hello User!!";
	}
}

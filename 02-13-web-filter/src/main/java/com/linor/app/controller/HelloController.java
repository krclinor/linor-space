package com.linor.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/hello2")
	public String sayHello1() {
		return "Hello World 2";
	}

	@GetMapping("/user/hello2")
	public String sayHello2() {
		return "Hello World 2";
	}
}

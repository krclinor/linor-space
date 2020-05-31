package com.linor.singer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linor.singer.service.HelloService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	private final HelloService hellService;
	
	@RequestMapping("/")
	public String welcom(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}
	
	@RequestMapping("hello")
	public String sayHello(Model model) {
		model.addAttribute("message", hellService.getHelloMessage());
		return "hello";
	}
}

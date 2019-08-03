package com.linor.singer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
	@RequestMapping(value="hello")
	public ModelAndView sayHello() {
		ModelAndView mView = new ModelAndView();
		mView.addObject("message", hellService.getHelloMessage());
		mView.setViewName("hello");
		return mView;
	}
}

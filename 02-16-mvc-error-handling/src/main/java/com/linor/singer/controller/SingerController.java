package com.linor.singer.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.linor.singer.exception.ResourceNotFoundException;

@Controller
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	@RequestMapping("/")
	public String welcom(Map<String, Object> model) {
		
		if(message == null)
			throw new ResourceNotFoundException();
		
		model.put("message", this.message);
		return "welcome";
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ModelAndView handleResourceNotFoundException(ResourceNotFoundException e) {
		ModelAndView model = new ModelAndView("error/404");
		model.addObject("exception", e);
		return model;
	}
}

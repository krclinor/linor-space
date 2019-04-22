package com.linor.singer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.linor.singer.model.Gender;
import com.linor.singer.model.User;

@Controller
public class UserController {
	private static final String[] countries =
		{"대한민국", "터어키", "미국", "일본"};
	
	@RequestMapping(value="/form")
	public ModelAndView user() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("user", new User());
		modelAndView.addObject("genders", Gender.values());
		modelAndView.addObject("countries", countries);
		modelAndView.setViewName("userForm");
		return modelAndView;
	}
	
	@RequestMapping(value = "/result")
	public ModelAndView processUser(User user) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("userResult");
		modelAndView.addObject("u", user);
		return modelAndView;
	}
}

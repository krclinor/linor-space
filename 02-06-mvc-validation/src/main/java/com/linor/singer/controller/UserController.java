package com.linor.singer.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linor.singer.model.Gender;
import com.linor.singer.model.User;
import com.linor.singer.validators.UserValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserValidator userValidator;
	
	private static final String[] countries =
		{"대한민국", "터어키", "미국", "일본"};
	
	@RequestMapping("/")
	public String index() {
		return "redirect:/form";
	}
	
	@RequestMapping(value="/form")
	public String user(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("genders", Gender.values());
		model.addAttribute("countries", countries);
		return "userForm";
	}
	
	@RequestMapping(value = "/result")
	public String processUser(@Valid User user, BindingResult result, Model model) {
		userValidator.validate(user, result);
		
		if(result.hasErrors()) {
			model.addAttribute("user", user);
			model.addAttribute("genders", Gender.values());
			model.addAttribute("countries", countries);
			return "userForm";
		}else {
			model.addAttribute("u", user);
			return "userResult";
		}
	}
}

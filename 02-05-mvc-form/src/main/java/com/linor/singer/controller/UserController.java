package com.linor.singer.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linor.singer.model.Gender;
import com.linor.singer.model.User;

@Controller
public class UserController {
	private static final String[] countries =
		{"대한민국", "터어키", "미국", "일본"};
	
	@RequestMapping(value="/form")
	public String user(Model model) {
		User user = User.builder()
				.country("대한민국")
				.birthDate(LocalDate.parse("2019-01-01"))
				.lastName("노")
				.name("병기")
				.password("secret")
				.email("linor@ekr.or.kr")
				.gender(Gender.MALE)
				.nonSmoking(true)
				.salary(300000)
				.build();
		model.addAttribute("user", new User());
		model.addAttribute("genders", Gender.values());
		model.addAttribute("countries", countries);
		return "userForm";
	}
//	public ModelAndView user() {
//	ModelAndView modelAndView = new ModelAndView();
//	modelAndView.addObject("user", new User());
//	modelAndView.addObject("genders", Gender.values());
//	modelAndView.addObject("countries", countries);
//	modelAndView.setViewName("userForm");
//	return modelAndView;
//}
	
	@RequestMapping(value = "/result")
	public String processUser(User user, Model model) {
		model.addAttribute("u", user);
		return "userResult";
	}
//	public ModelAndView processUser(User user) {
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("userResult");
//		modelAndView.addObject("u", user);
//		return modelAndView;
//	}
}

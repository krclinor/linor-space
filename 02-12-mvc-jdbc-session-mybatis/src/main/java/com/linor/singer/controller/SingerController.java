package com.linor.singer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SingerController {
	@Value("${welcome.message:test}")
	private String message;
	
	@Autowired
	MessageSource messageSource;
	
	@RequestMapping("/")
	public String welcom(Map<String, Object> model) {
		model.put("message", this.message);
		return "welcome";
	}

	@GetMapping("/colors")
	public String index(Model model, HttpSession session) {
		List<String> favoriteColors = getFavoritColors(session);
		model.addAttribute("favoriteColors", favoriteColors);
		model.addAttribute("sessionId", session.getId());
		return "colors";
	}
	
	private List<String> getFavoritColors(HttpSession session){
		List<String> favoritColors = (List<String>)session.getAttribute("favoriteColors");
		if(favoritColors == null) {
			favoritColors = new ArrayList<>();
		}
		return favoritColors;
	}
	
	@RequestMapping("/saveColor")
	public String saveMessage(@ModelAttribute("color") String color, HttpSession session) {
		List<String> favoritColors = getFavoritColors(session);
		if(!StringUtils.isEmpty(color)) {
			favoritColors.add(color);
			session.setAttribute("favoriteColors", favoritColors);
		}
		return "redirect:/colors";
	}
	
	@GetMapping("/closeSession")
	public String closeSession(HttpSession session) {
		session.invalidate();
		return "redirect:/colors";
	}
}

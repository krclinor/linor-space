package com.linor.singer.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SingerController {
	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		List<String> favoriteColors = getFavoritColors(session);
		model.addAttribute("favoriteColors", favoriteColors);
		model.addAttribute("sessionId", session.getId());
		return "index";
	}
	
	private List<String> getFavoritColors(HttpSession session){
		List<String> favoritColors = (List<String>)session.getAttribute("favoriteColors");
		if(favoritColors == null) {
			favoritColors = new ArrayList<>();
		}
		return favoritColors;
	}
	
	@PostMapping("/saveColor")
	public String saveMessage(@RequestParam("color") String color, HttpSession session) {
		List<String> favoritColors = getFavoritColors(session);
		if(!StringUtils.isEmpty(color)) {
			favoritColors.add(color);
			session.setAttribute("favoriteColors", favoritColors);
		}
		return "redirect:/";
	}
	
	@GetMapping("/closeSession")
	public String closeSession(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}

package com.linor.singer.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linor.singer.domain.ChangePassword;
import com.linor.singer.domain.JwtAuthUser;
import com.linor.singer.domain.UserTokenState;
import com.linor.singer.security.TokenHelper;
import com.linor.singer.serviceImpl.UserDetailsServiceImpl;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
	@Autowired
	TokenHelper tokenHelper;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(
			@RequestBody JwtAuthUser authUser){
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authUser.getUsername(), 
						authUser.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		User user = (User)authentication.getPrincipal();
		String jws = tokenHelper.generateToken(user.getUsername());
		int expiresIn = tokenHelper.getExpiredIn();
		
		return ResponseEntity.ok(new UserTokenState(jws, expiresIn));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAuthenticationToken(
			HttpServletRequest request,
			HttpServletResponse response,
			Principal principal){
		String authToken = tokenHelper.getToken(request);
		if(authToken != null && principal != null) {
			String refreshToken = tokenHelper.refreshToken(authToken);
			int expiredIn = tokenHelper.getExpiredIn();
			
			return ResponseEntity.ok(new UserTokenState(refreshToken, expiredIn));
		}else {
			UserTokenState userTokenState = new UserTokenState();
			return ResponseEntity.ok(userTokenState);
		}
	}
	
//	public ResponseEntity<?> changePassword(@RequestBody ChangePassword passwords) {
//		userDetailsService.changePassword(passwords.getOldPassword(), passwords.getNewPassword());
//		Map<String, String> result = new HashMap<>();
//		result.put("result", "success");
//		return ResponseEntity.accepted().body(result);
//	}
}

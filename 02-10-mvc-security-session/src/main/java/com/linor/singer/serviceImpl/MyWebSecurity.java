package com.linor.singer.serviceImpl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class MyWebSecurity {
	public boolean checkUserId(Authentication auth, String userId, HttpServletRequest request) {
		if(userId.equals(auth.getName())) {
			return true;
		}
		return false;
	}
}

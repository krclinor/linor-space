package com.linor.singer.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.linor.singer.domain.JwtAuthentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final TokenHelper tokenHelper;
	private final UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String username;
		String authToken = tokenHelper.getToken(request);
		
		if(authToken != null) {
			username = tokenHelper.getUsernameFromToken(authToken);
			if(username != null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if(tokenHelper.validateToken(authToken, userDetails)) {
					JwtAuthentication authentication = new JwtAuthentication(userDetails);
					authentication.setToken(authToken);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}

}

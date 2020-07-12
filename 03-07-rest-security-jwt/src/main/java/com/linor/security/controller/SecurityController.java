package com.linor.security.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.linor.security.auth.JwtAuthenticationToken;
import com.linor.security.auth.jwt.TokenExtractor;
import com.linor.security.auth.jwt.TokenVerifier;
import com.linor.security.config.JwtSettings;
import com.linor.security.config.WebSecurityConfig;
import com.linor.security.exceptions.InvalidJwtToken;
import com.linor.security.model.MyUser;
import com.linor.security.model.UserContext;
import com.linor.security.model.token.JwtTokenFactory;
import com.linor.security.model.token.RawAccessJwtToken;
import com.linor.security.model.token.RefreshToken;
import com.linor.security.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SecurityController {
	private final JwtTokenFactory tokenFactory;
	private final JwtSettings jwtSettings;
	private final UserService userService;
	private final TokenVerifier tokenVerifier;
	private final TokenExtractor tokenExtractor;

	@GetMapping("/rest/me")
	public @ResponseBody UserContext get(JwtAuthenticationToken token) {
		return (UserContext) token.getPrincipal();
	}

	@GetMapping("/auth/token")
	public @ResponseBody Map<String, String> refreshToken(HttpServletRequest request)
			throws IOException, ServletException {
		String tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));

		RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
		RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getKey())
				.orElseThrow(() -> new InvalidJwtToken());

		String jti = refreshToken.getJti();
		if (!tokenVerifier.verify(jti)) {
			throw new InvalidJwtToken();
		}

		String subject = refreshToken.getSubject();
		MyUser user = userService.getByUsername(subject)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

		if (user.getRoles() == null)
			throw new InsufficientAuthenticationException("User has no roles assigned");
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getId())).collect(Collectors.toList());

		UserContext userContext = UserContext.create(user.getId(), authorities);

		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("token", tokenFactory.createAccessJwtToken(userContext).getToken());
		tokenMap.put("tokenExpirationTime", jwtSettings.getTokenExpirationTime().toString());

		return tokenMap;
	}
}

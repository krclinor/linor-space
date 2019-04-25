package com.linor.singer.security;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.linor.singer.dao.UserDao;
import com.linor.singer.domain.MyUser;

import ch.qos.logback.core.net.server.Client;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
public class TokenHelper {
	@Autowired
	private JwtProperties jwtProperties;
	
	@Autowired
	private UserDao userDao;
	
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
	
	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		
		return username;
	}
	
	public Date getIssuedAtDateFromToken(String token) {
		Date issuedAt;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			issuedAt = claims.getIssuedAt();
		}catch (Exception e) {
			issuedAt = null;
		}
		return issuedAt;
	}
	
	public String getAudianceFromToken(String token) {
		String audiance;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			audiance = claims.getAudience();
		}catch (Exception e) {
			audiance = null;
		}
		return audiance;
	}
	
	public String refreshToken(String token) {
		String refreshedToken;
		Date a = new Date();
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			claims.setIssuedAt(a);
			refreshedToken = Jwts.builder()
					.setClaims(claims)
					.setExpiration(generateExpirationDate())
					.signWith(signatureAlgorithm, jwtProperties.getSecret())
					.compact();
		}catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}
	
	public String generateToken(String username) {
		return Jwts.builder()
				.setIssuer(jwtProperties.getAppName())
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(signatureAlgorithm, jwtProperties.getSecret())
				.compact();
	}
	
	public int getExpiredIn() {
		return jwtProperties.getExpiresIn();
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		MyUser myUser = userDao.findById(userDetails.getUsername());
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		return (username != null 
				&& username.equals(myUser.getId())
				&& !isCreatedBeforeLastPasswordReset(created, myUser.getLastPasswordResetDate()));
	}
	
	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswrodReset) {
		return (lastPasswrodReset != null && created.before(lastPasswrodReset));
	}
	
	public String getToken(HttpServletRequest request) {
		String authHeader= request.getHeader(jwtProperties.getAuthHeader());
		if(authHeader != null && authHeader.startsWith(jwtProperties.getHeaderPrefix() + " ")) {
			return authHeader.substring(jwtProperties.getHeaderPrefix().length() + 1);
		}
		return null;
	}
	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(jwtProperties.getSecret())
					.parseClaimsJws(token)
					.getBody();
		}catch(Exception e) {
			claims = null;
		}
		return claims;
	}
	
	private Date generateExpirationDate() {
		if(jwtProperties.getExpiresIn() > 0) {
			return new Date((new Date()).getTime() + jwtProperties.getExpiresIn() * 1000);
		}
		return null;
	}
}

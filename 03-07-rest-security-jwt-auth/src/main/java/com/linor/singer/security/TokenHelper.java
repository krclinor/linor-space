package com.linor.singer.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.linor.singer.config.JwtProperties;
import com.linor.singer.dao.UserDao;
import com.linor.singer.domain.MyUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenHelper {
	private static final String AUTH_HEADER = "Authorization";
	private static final String HEADER_PREFIX = "Bearer";
	private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	private final JwtProperties jwtProperties;
	private final UserDao userDao;

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
		} catch (Exception e) {
			issuedAt = null;
		}
		return issuedAt;
	}

	public String getAudianceFromToken(String token) {
		String audiance;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			audiance = claims.getAudience();
		} catch (Exception e) {
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
			refreshedToken = Jwts.builder().setClaims(claims).setExpiration(getExpirationDate())
					.signWith(jwtProperties.getKey(), SIGNATURE_ALGORITHM).compact();
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	public String generateToken(User user) {
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("scopes", user.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));

		LocalDateTime currentTime = LocalDateTime.now();
		return Jwts.builder().setClaims(claims).setIssuer(jwtProperties.getIssure())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(jwtProperties.getExpiresIn())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(jwtProperties.getKey(), SignatureAlgorithm.HS512).compact();
	}

	public String generateRefreshToken(User user) {
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("scopes", user.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));

		LocalDateTime currentTime = LocalDateTime.now();
		return Jwts.builder().setClaims(claims).setIssuer(jwtProperties.getIssure())
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(jwtProperties.getRefreshExpiresIn())
						.atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(jwtProperties.getKey(), SignatureAlgorithm.HS512).compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		MyUser myUser = userDao.findById(userDetails.getUsername());
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		return (username != null && username.equals(myUser.getId())
				&& !isCreatedBeforeLastPasswordReset(created, myUser.getLastPasswordResetDate()));
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswrodReset) {
		return (lastPasswrodReset != null && created.before(lastPasswrodReset));
	}

	public String getToken(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTH_HEADER);
		if (authHeader != null && authHeader.startsWith(HEADER_PREFIX + " ")) {
			return authHeader.substring(HEADER_PREFIX.length() + 1);
		}
		return null;
	}

	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parserBuilder().setSigningKey(jwtProperties.getKey()).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	private Date getExpirationDate() {
		if (jwtProperties.getExpiresIn() > 0) {
			return new Date((new Date()).getTime() + jwtProperties.getExpiresIn() * 1000);
		}
		return null;
	}
}

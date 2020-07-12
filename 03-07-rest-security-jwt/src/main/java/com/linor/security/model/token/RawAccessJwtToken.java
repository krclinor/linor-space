package com.linor.security.model.token;

import java.security.Key;

import org.springframework.security.authentication.BadCredentialsException;

import com.linor.security.exceptions.JwtExpiredTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RawAccessJwtToken implements JwtToken {
	@Getter
	private final String token;

	/**
	 * Parses and validates JWT Token signature.
	 * 
	 * @throws BadCredentialsException
	 * @throws JwtExpiredTokenException
	 * 
	 */
	public Jws<Claims> parseClaims(Key key) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(this.token);
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SecurityException ex) {
			log.error("Invalid JWT Token", ex);
			throw new BadCredentialsException("Invalid JWT token: ", ex);
		} catch (ExpiredJwtException expiredEx) {
			log.info("JWT Token is expired", expiredEx);
			throw new JwtExpiredTokenException(this, "JWT Token expired", expiredEx);
		}
	}

}

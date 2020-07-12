package com.linor.security.model.token;

import java.security.Key;
import java.util.List;
import java.util.Optional;

import com.linor.security.model.Scopes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshToken implements JwtToken {
	@Getter
	private final Jws<Claims> claims;

	public static Optional<RefreshToken> create(RawAccessJwtToken token, Key key) {
		Jws<Claims> claims = token.parseClaims(key);

		List<String> scopes = claims.getBody().get("scopes", List.class);
		if (scopes == null || scopes.isEmpty() || !scopes.stream()
				.filter(scope -> Scopes.REFRESH_TOKEN.authority().equals(scope)).findFirst().isPresent()) {
			return Optional.empty();
		}

		return Optional.of(new RefreshToken(claims));
	}

	@Override
	public String getToken() {
		return null;
	}

	public String getJti() {
		return claims.getBody().getId();
	}

	public String getSubject() {
		return claims.getBody().getSubject();
	}
}

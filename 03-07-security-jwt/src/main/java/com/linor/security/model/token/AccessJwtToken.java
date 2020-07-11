package com.linor.security.model.token;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class AccessJwtToken implements JwtToken {
    private final String rawToken;
    
    @JsonIgnore
    @Getter
    private Claims claims;

    public String getToken() {
        return this.rawToken;
    }
}

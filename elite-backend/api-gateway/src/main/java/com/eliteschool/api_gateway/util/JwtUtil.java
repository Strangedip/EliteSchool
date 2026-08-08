package com.eliteschool.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    private SecretKey key;

    private static final String ROLE_CLAIM_KEY = "role";
    private static final String USER_ID_CLAIM_KEY = "userId";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get(ROLE_CLAIM_KEY, String.class);
    }

    public String extractUserId(String token) {
        return parseClaims(token).get(USER_ID_CLAIM_KEY, String.class);
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new java.util.Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Optional<String> extractTokenFromCookies(ServerHttpRequest request) {
        List<HttpCookie> cookies = request.getCookies().get(cookieName);
        if (cookies == null || cookies.isEmpty()) {
            return Optional.empty();
        }
        return cookies.stream().findFirst().map(HttpCookie::getValue);
    }
}

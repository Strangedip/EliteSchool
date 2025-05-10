package com.eliteschool.api_gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    private Key key;

    private static final String ROLE_CLAIM_KEY = "role";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT Token with username and role
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim(ROLE_CLAIM_KEY, role)  // Add role claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract Username from JWT Token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract Role from JWT Token
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(ROLE_CLAIM_KEY, String.class);  // Get role from claims
    }

    // Validate JWT Token
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    // Get JWT token from the Authorization header
    public Optional<String> extractTokenFromHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    // Get JWT token from cookie
    public Optional<String> extractTokenFromCookies(ServerHttpRequest request) {
        List<HttpCookie> cookies = request.getCookies().get(cookieName);
        if (cookies == null || cookies.isEmpty()) {
            return Optional.empty();
        }

        return cookies.stream()
                .findFirst()
                .map(HttpCookie::getValue);
    }

    // Set JWT token as HttpOnly cookie in a reactive manner
    public void setTokenCookie(ServerHttpResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true) // Set to false for local testing without HTTPS
                .path("/")
                .maxAge((int) (expiration / 1000)) // expiration time in seconds
                .build();

        response.addCookie(cookie);
    }

    // Clear JWT token cookie
    public void clearTokenCookie(ServerHttpResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true) // Set to false for local testing without HTTPS
                .path("/")
                .maxAge(0) // Remove cookie
                .build();

        response.addCookie(cookie);
    }
}

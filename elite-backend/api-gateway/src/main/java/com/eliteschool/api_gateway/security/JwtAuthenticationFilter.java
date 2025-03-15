package com.eliteschool.api_gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header found or invalid format");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        logger.info("Extracted Token: {}", token);

        if (!jwtUtil.validateToken(token)) {
            logger.error("Invalid JWT token detected!");
            return unauthorizedResponse(exchange);
        }

        Claims claims = jwtUtil.extractClaims(token);
        if (claims == null) {
            logger.error("Failed to extract claims from JWT token.");
            return unauthorizedResponse(exchange);
        }

        String username = claims.getSubject();
        logger.info("User '{}' authenticated successfully via JWT.", username);

        // Modify request to forward Authorization header properly
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader) // Forward the token
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}

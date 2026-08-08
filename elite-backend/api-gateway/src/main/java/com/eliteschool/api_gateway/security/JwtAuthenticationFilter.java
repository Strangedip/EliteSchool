package com.eliteschool.api_gateway.security;

import com.eliteschool.api_gateway.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = null;

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            Optional<String> cookieToken = jwtUtil.extractTokenFromCookies(request);
            if (cookieToken.isPresent()) {
                token = cookieToken.get();
            }
        }

        if (token == null) {
            ServerHttpRequest cleaned = stripSpoofedIdentityHeaders(exchange.getRequest());
            return chain.filter(exchange.mutate().request(cleaned).build());
        }

        String username;
        String role;
        String userId;
        try {
            username = jwtUtil.extractUsername(token);
            role = jwtUtil.extractRole(token);
            userId = jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return unauthorizedResponse(exchange);
        }

        if (ObjectUtils.isEmpty(username) || !jwtUtil.validateToken(token, username)) {
            return unauthorizedResponse(exchange);
        }

        ServerHttpRequest.Builder requestBuilder = stripSpoofedIdentityHeaders(exchange.getRequest()).mutate()
                .header("e-username", username)
                .header("e-user-role", role != null ? role : "");
        if (!ObjectUtils.isEmpty(userId)) {
            requestBuilder.header("e-user-id", userId);
        }
        ServerHttpRequest mutatedRequest = requestBuilder.build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ELITE")));
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    private ServerHttpRequest stripSpoofedIdentityHeaders(ServerHttpRequest request) {
        return request.mutate().headers(headers -> {
            headers.remove("e-internal-service");
            headers.remove("e-username");
            headers.remove("e-user-role");
            headers.remove("e-user-id");
        }).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}

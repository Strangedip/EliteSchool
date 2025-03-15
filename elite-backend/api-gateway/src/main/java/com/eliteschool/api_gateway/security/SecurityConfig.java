package com.eliteschool.api_gateway.config;

import com.eliteschool.api_gateway.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // 🔹 Disable CSRF
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/auth/**").permitAll()  // 🔹 Allow public access to Auth APIs
                        .anyExchange().authenticated()  // 🔹 Secure other endpoints (JWT required)
                )
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION) // 🔹 Apply JWT filter
                .build();
    }
}

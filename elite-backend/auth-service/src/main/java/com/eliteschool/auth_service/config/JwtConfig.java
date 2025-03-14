package com.eliteschool.auth_service.config;

import com.eliteschool.auth_service.security.JwtAuthenticationFilter;
import com.eliteschool.auth_service.security.JwtUtil;
import com.eliteschool.auth_service.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }
}

package com.eliteschool.auth_service.security;

import com.eliteschool.auth_service.model.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testUsername;
    private RoleType testRole;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "wJjY8fXo+G6Vkhd4yLh3JOp6tH8l29PY5zR0DboYdxM=");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtUtil, "cookieName", "AUTH_TOKEN");
        jwtUtil.init();

        testUsername = "testuser";
        testRole = RoleType.STUDENT;
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        boolean isValid = jwtUtil.validateToken(token, testUsername);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject token with wrong username")
    void shouldRejectTokenWithWrongUsername() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        boolean isValid = jwtUtil.validateToken(token, "differentuser");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtUtil.generateToken("user1", RoleType.STUDENT);
        String token2 = jwtUtil.generateToken("user2", RoleType.STUDENT);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void shouldGenerateDifferentTokensForDifferentRoles() {
        String studentToken = jwtUtil.generateToken(testUsername, RoleType.STUDENT);
        String facultyToken = jwtUtil.generateToken(testUsername, RoleType.FACULTY);

        assertThat(studentToken).isNotEqualTo(facultyToken);
    }
}

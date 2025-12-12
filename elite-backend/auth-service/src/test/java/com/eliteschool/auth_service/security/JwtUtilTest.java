package com.eliteschool.auth_service.security;

import com.eliteschool.auth_service.model.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=wJjY8fXo+G6Vkhd4yLh3JOp6tH8l29PY5zR0DboYdxM=",
    "jwt.expiration=3600000"
})
@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private String testUsername;
    private RoleType testRole;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        testRole = RoleType.STUDENT;
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        // Act
        String token = jwtUtil.generateToken(testUsername, testRole);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername, testRole);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername, testRole);

        // Act
        boolean isValid = jwtUtil.validateToken(token, testUsername);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject token with wrong username")
    void shouldRejectTokenWithWrongUsername() {
        // Arrange
        String token = jwtUtil.generateToken(testUsername, testRole);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "differentuser");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        // Act
        String token1 = jwtUtil.generateToken("user1", RoleType.STUDENT);
        String token2 = jwtUtil.generateToken("user2", RoleType.STUDENT);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void shouldGenerateDifferentTokensForDifferentRoles() {
        // Act
        String studentToken = jwtUtil.generateToken(testUsername, RoleType.STUDENT);
        String facultyToken = jwtUtil.generateToken(testUsername, RoleType.FACULTY);

        // Assert
        assertThat(studentToken).isNotEqualTo(facultyToken);
    }
}


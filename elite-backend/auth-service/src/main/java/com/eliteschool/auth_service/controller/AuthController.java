package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.mapper.UserMapper;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.security.JwtUtil;
import com.eliteschool.auth_service.service.UserService;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()) || userService.existsByUsername(user.getUsername())) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "USER_EXISTS",
                    "Email or Username already exists", "Registration failed");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.createUser(user);

        return ResponseUtil.success("User registered successfully", UserMapper.toResponseDTO(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return userService.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername(),user.getRole() );
                    response.setHeader("Authorization", "Bearer " + token);
                    return ResponseUtil.success("Login successful", Map.of(
                        "token", token,
                        "user", UserMapper.toResponseDTO(user)
                    ));
                })
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Invalid username or password", "Login failed"));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Token missing", null);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (ObjectUtils.isEmpty(username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        if (!jwtUtil.validateToken(token, username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        // Return user details along with the validation
        try {
            User user = userService.getUserEntityByUsername(username);
            return ResponseUtil.success("User validated successfully", UserMapper.toResponseDTO(user));
        } catch (Exception e) {
            return ResponseUtil.success("User validated successfully, but profile data not available", null);
        }
    }

    /**
     * Get the current user's profile
     * This endpoint uses the JWT token to identify the user and return their profile data
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Token missing", null);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (ObjectUtils.isEmpty(username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        if (!jwtUtil.validateToken(token, username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        try {
            User user = userService.getUserEntityByUsername(username);
            return ResponseUtil.success("User profile retrieved successfully", UserMapper.toResponseDTO(user));
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", 
                "User profile not found: " + e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // Clear the JWT token by setting the cookie to expire
        jwtUtil.clearTokenCookie(response);
        return ResponseUtil.success("User logged out successfully", null);
    }
}

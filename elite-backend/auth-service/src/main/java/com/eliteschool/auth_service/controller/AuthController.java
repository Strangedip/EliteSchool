package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.dto.request.LoginRequestDTO;
import com.eliteschool.auth_service.dto.request.UserRequestDTO;
import com.eliteschool.auth_service.mapper.UserMapper;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.security.JwtUtil;
import com.eliteschool.auth_service.service.UserService;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userDTO) {
        if (userService.existsByEmail(userDTO.getEmail()) || userService.existsByUsername(userDTO.getUsername())) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "USER_EXISTS",
                    "Email or Username already exists", "Registration failed");
        }

        User user = UserMapper.fromRequestDTO(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userService.createUser(user);

        return ResponseUtil.success("User registered successfully", UserMapper.toResponseDTO(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        return userService.findByUsername(loginRequest.getUsername())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
                    response.setHeader("Authorization", "Bearer " + token);
                    return ResponseUtil.success("Login successful", Map.of(
                        "token", token,
                        "user", UserMapper.toResponseDTO(user)
                    ));
                })
                .orElseGet(() -> ResponseUtil.error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Invalid username or password", "Login failed"));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Token missing", null);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (ObjectUtils.isEmpty(username) || !jwtUtil.validateToken(token, username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        try {
            User user = userService.getUserEntityByUsername(username);
            return ResponseUtil.success("Token valid", UserMapper.toResponseDTO(user));
        } catch (Exception e) {
            return ResponseUtil.success("Token valid", null);
        }
    }

    // Returns user profile using JWT token for identification
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Token missing", null);
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (ObjectUtils.isEmpty(username) || !jwtUtil.validateToken(token, username)) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "FAILED_AUTHORIZATION", "Invalid Token", null);
        }

        try {
            User user = userService.getUserEntityByUsername(username);
            return ResponseUtil.success("Profile retrieved", UserMapper.toResponseDTO(user));
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                "User not found: " + e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        jwtUtil.clearTokenCookie(response);
        return ResponseUtil.success("Logged out successfully", null);
    }
}

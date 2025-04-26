package com.eliteschool.auth_service.controller;

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
        if (userService.emailExists(user.getEmail()) || userService.usernameExists(user.getUsername())) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "USER_EXISTS",
                    "Email or Username already exists", "Registration failed");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.saveUser(user);

        return ResponseUtil.success("User registered successfully", null);
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
                    return ResponseUtil.success("Login successful", Map.of("token", token));
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

        return ResponseUtil.success("User validated successfully", null);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // Clear the JWT token by setting the cookie to expire
        jwtUtil.clearTokenCookie(response);
        return ResponseUtil.success("User logged out successfully", null);
    }
}

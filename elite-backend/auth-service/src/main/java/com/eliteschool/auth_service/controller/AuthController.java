package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.security.JwtUtil;
import com.eliteschool.auth_service.service.UserService;
import com.eliteschool.common_utils.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return userService.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(username);
                    return ResponseUtil.success("Login successful", Map.of("token", token));
                })
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Invalid username or password", "Login failed"));
    }
}

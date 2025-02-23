package com.school.elite.controller;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.service.AuthService;
import com.school.elite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    /**
     * Endpoint to validate user credentials (Login).
     */
    @GetMapping("/login")
    public ResponseEntity<CommonResponseDto> validateUserCredential(
            @RequestParam String username,
            @RequestParam String password) {

        logger.info("Login request received for username: {}", username);
        CommonResponseDto response = authService.validateUserCredential(username, password);
        return ResponseEntity.ok(response);
    }
}

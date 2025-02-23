package com.school.elite.controller;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    UserService eliteUserService;

    @GetMapping(path = "/login")
    public ResponseEntity<CommonResponseDto> validateUserCredential(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        logger.info("Login request for username : {}", username);
        return new ResponseEntity<>(eliteUserService.validateUserCredential(username, password), HttpStatus.OK);
    }
}

package com.school.elite.controller;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.repository.TaskRepo;
import com.school.elite.repository.UserRepo;
import com.school.elite.service.UserService;
import com.school.elite.utils.Utils;
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
    UserRepo userRepo;

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    UserService eliteUserService;

    @GetMapping(path = "/login")
    public ResponseEntity<CommonResponseDto> create(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password) {
        logger.info(Utils.logSeparator());
        logger.info("Login request for username : {}", username);
        return new ResponseEntity<>(eliteUserService.validateUserCredential(username, password), HttpStatus.OK);
    }
}

package com.school.elite.controller;

import com.school.elite.DTO.EliteResponse;
import com.school.elite.DTO.UserCreateRequestDTO;
import com.school.elite.repository.EliteTaskRepo;
import com.school.elite.repository.EliteUserRepo;
import com.school.elite.service.UserService;
import com.school.elite.utils.Constants;
import com.school.elite.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    EliteUserRepo userRepo;
    @Autowired
    EliteTaskRepo eliteTaskRepo;

    @Autowired
    UserService eliteUserService;

    @GetMapping(path = "/status")
    public String status() {
        return "Running";
    }

    @GetMapping(path = "/create-user")
    public ResponseEntity<EliteResponse> create(@RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        logger.info(Utils.logSeparator());
        logger.info("Received Request to create User : {}", userCreateRequestDTO);
        return new ResponseEntity<>(eliteUserService.createNewUser(userCreateRequestDTO), HttpStatus.CREATED);
    }
}

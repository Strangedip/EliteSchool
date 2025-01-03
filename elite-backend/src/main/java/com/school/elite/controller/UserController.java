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
@RequestMapping(path = "/api/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    TaskRepo taskRepo;

    @Autowired
    UserService eliteUserService;

    @PostMapping(path = "/create")
    public ResponseEntity<CommonResponseDto> create(@RequestBody UserCreateRequestDto userCreateRequestDTO) {
        logger.info(Utils.logSeparator());
        logger.info("Received Request to create User : {}", userCreateRequestDTO);
        return new ResponseEntity<>(eliteUserService.createNewUser(userCreateRequestDTO), HttpStatus.CREATED);
    }
}

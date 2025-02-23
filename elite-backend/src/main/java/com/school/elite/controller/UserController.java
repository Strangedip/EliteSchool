package com.school.elite.controller;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.DTO.UserUpdateRequestDto;
import com.school.elite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Endpoint to create a new user.
     */
    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateRequestDto) {
        logger.info("Received request to create user: {}", userCreateRequestDto);
        CommonResponseDto response = userService.createNewUser(userCreateRequestDto);
        return ResponseEntity.status(response.getResponseCode()).body(response);
    }

    /**
     * Endpoint to update an existing user.
     */
    @PutMapping("/update/{eliteId}")
    public ResponseEntity<CommonResponseDto> updateUser(@PathVariable String eliteId, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        logger.info("Received request to update user with ID: {}", eliteId);
        CommonResponseDto response = userService.updateUser(eliteId, userUpdateRequestDto);
        return ResponseEntity.status(response.getResponseCode()).body(response);
    }
}

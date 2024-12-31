package com.school.elite.service.impl;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDTO;
import com.school.elite.entity.User;
import com.school.elite.repository.dbservice.EliteDBService;
import com.school.elite.service.UserService;
import com.school.elite.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    EliteDBService eliteDBService;

    @Autowired
    PasswordEncoder bcrypt;

    @Override
    public CommonResponseDto createNewUser(UserCreateRequestDTO createRequestDTO) {
        try {
            eliteDBService.saveUser(convertRequestDtoToEntity(createRequestDTO));
            return CommonResponseDto.builder()
                    .responseCode(HttpStatus.CREATED.value())
                    .responseMessage("Elite user created successfully.")
                    .build();
        } catch (Exception e) {
            return CommonResponseDto.builder()
                    .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .errorMessage("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    private User convertRequestDtoToEntity(UserCreateRequestDTO requestDTO) {
        return new User(
                Utils.createUUID(),
                requestDTO.getName(),
                requestDTO.getAge(),
                requestDTO.getGender(),
                requestDTO.getEmail(),
                requestDTO.getMobileNumber(),
                requestDTO.getPosition(),
                requestDTO.getUsername(),
                encryptPassword(requestDTO.getPassword())
        );
    }

    private String encryptPassword(String password) {
        return bcrypt.encode(password);
    }
}

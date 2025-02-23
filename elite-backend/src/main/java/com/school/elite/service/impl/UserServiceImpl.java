package com.school.elite.service.impl;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.entity.User;
import com.school.elite.exception.UserExceptions;
import com.school.elite.repository.UserRepository;
import com.school.elite.service.UserService;
import com.school.elite.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder bcrypt;

    @Override
    public CommonResponseDto createNewUser(UserCreateRequestDto createRequestDTO) {
        userRepository.save(convertRequestDtoToEntity(createRequestDTO));
        return CommonResponseDto.builder()
                .responseCode(HttpStatus.CREATED.value())
                .responseMessage("Elite user created successfully.")
                .build();

    }

    @Override
    public CommonResponseDto validateUserCredential(String username, String password) {
        if (isUserCredentialValid(username, password)) {
            return CommonResponseDto.createCommonResponseDto(HttpStatus.OK.value(), "Valid Credentials", null, null);
        }
        throw new UserExceptions.InvalidUserCredentialException("Invalid Credentials");
    }

    public boolean isUserCredentialValid(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserExceptions.UserNotFoundException("User with username '" + username + "' not found"));
        if (!bcrypt.matches(password, user.getPassword())) {
            throw new UserExceptions.InvalidUserCredentialException("Invalid credentials. Please check and retry.");
        }
        return true;
    }

    private User convertRequestDtoToEntity(UserCreateRequestDto requestDTO) {
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

package com.school.elite.service.impl;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.entity.User;
import com.school.elite.exception.UserException;
import com.school.elite.repository.UserRepository;
import com.school.elite.service.UserService;
import com.school.elite.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CommonResponseDto createNewUser(UserCreateRequestDto createRequestDTO) {
        if (userRepository.findByUsername(createRequestDTO.getUsername()).isPresent()) {
            throw new UserException.InvalidUserException("Username already exists.");
        }

        if (userRepository.findByEmail(createRequestDTO.getEmail()).isPresent()) {
            throw new UserException.InvalidUserException("Email already exists.");
        }

        User newUser = convertRequestDtoToEntity(createRequestDTO);
        userRepository.save(newUser);

        return CommonResponseDto.builder()
                .responseCode(HttpStatus.CREATED.value())
                .responseMessage("Elite user created successfully.")
                .build();
    }

    @Override
    public CommonResponseDto validateUserCredential(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("User with username '"+ username +"' not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException.InvalidUserCredentialException("Invalid credentials. Please check and retry.");
        }

        return CommonResponseDto.createCommonResponseDto(HttpStatus.OK.value(), "Valid Credentials", null, null);
    }

    private User convertRequestDtoToEntity(UserCreateRequestDto requestDTO) {
        return User.builder()
                .eliteId(Utils.createUUID())  // Generate UUID or another unique identifier
                .name(requestDTO.getName())
                .age(requestDTO.getAge())
                .gender(requestDTO.getGender())
                .email(requestDTO.getEmail())
                .mobileNumber(requestDTO.getMobileNumber())
                .position(requestDTO.getPosition())
                .username(requestDTO.getUsername())
                .password(encryptPassword(requestDTO.getPassword()))
                .build();
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}

package com.school.elite.service.impl;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.entity.User;
import com.school.elite.exception.UserException;
import com.school.elite.repository.UserRepository;
import com.school.elite.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CommonResponseDto validateUserCredential(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException.UserNotFoundException("User with username '" + username + "' not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException.InvalidUserCredentialException("Invalid credentials. Please check and retry.");
        }

        return CommonResponseDto.createCommonResponseDto(HttpStatus.OK.value(), "Valid Credentials", null, null);
    }
}

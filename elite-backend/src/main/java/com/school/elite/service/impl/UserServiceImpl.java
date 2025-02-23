package com.school.elite.service.impl;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.DTO.UserUpdateRequestDto;
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
    private final PasswordEncoder bcrypt;

    @Override
    public CommonResponseDto createNewUser(UserCreateRequestDto createRequestDTO) {
        userRepository.save(convertRequestDtoToEntity(createRequestDTO));
        return CommonResponseDto.builder()
                .responseCode(HttpStatus.CREATED.value())
                .responseMessage("Elite user created successfully.")
                .build();
    }

    @Override
    public CommonResponseDto updateUser(String eliteId, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findById(eliteId)
                .orElseThrow(() -> new UserException.UserNotFoundException("User with ID '" + eliteId + "' not found"));

        // Update user details
        if (userUpdateRequestDto.getName() != null) user.setName(userUpdateRequestDto.getName());
        if (userUpdateRequestDto.getAge() != null) user.setAge(userUpdateRequestDto.getAge());
        if (userUpdateRequestDto.getGender() != null) user.setGender(userUpdateRequestDto.getGender());
        if (userUpdateRequestDto.getEmail() != null) user.setEmail(userUpdateRequestDto.getEmail());
        if (userUpdateRequestDto.getMobileNumber() != null) user.setMobileNumber(userUpdateRequestDto.getMobileNumber());
        if (userUpdateRequestDto.getPosition() != null) user.setPosition(userUpdateRequestDto.getPosition());
        if (userUpdateRequestDto.getUsername() != null) user.setUsername(userUpdateRequestDto.getUsername());

        userRepository.save(user);

        return CommonResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .responseMessage("User updated successfully.")
                .build();
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

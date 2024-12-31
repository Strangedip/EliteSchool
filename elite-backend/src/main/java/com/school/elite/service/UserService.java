package com.school.elite.service;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;

public interface UserService {

    CommonResponseDto createNewUser(UserCreateRequestDto createRequestDTO);

    CommonResponseDto validateUserCredential(String username, String Password);
}

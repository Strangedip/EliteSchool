package com.school.elite.service;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDto;
import com.school.elite.DTO.UserUpdateRequestDto;

public interface UserService {
    CommonResponseDto createNewUser(UserCreateRequestDto createRequestDTO);
    CommonResponseDto updateUser(String eliteId, UserUpdateRequestDto userUpdateRequestDto);
}

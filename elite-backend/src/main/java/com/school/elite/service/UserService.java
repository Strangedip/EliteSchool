package com.school.elite.service;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.UserCreateRequestDTO;

public interface UserService {

    CommonResponseDto createNewUser(UserCreateRequestDTO createRequestDTO);
}

package com.school.elite.service;

import com.school.elite.DTO.EliteResponse;
import com.school.elite.DTO.UserCreateRequestDTO;

public interface UserService {

    EliteResponse createNewUser(UserCreateRequestDTO createRequestDTO);
}

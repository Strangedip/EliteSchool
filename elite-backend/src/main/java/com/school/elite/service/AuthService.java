package com.school.elite.service;

import com.school.elite.DTO.CommonResponseDto;

public interface AuthService {
    CommonResponseDto validateUserCredential(String username, String Password);
}

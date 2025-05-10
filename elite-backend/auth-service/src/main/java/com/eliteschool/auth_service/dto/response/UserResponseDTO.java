package com.eliteschool.auth_service.dto.response;

import com.eliteschool.auth_service.model.enums.Gender;
import com.eliteschool.auth_service.model.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponseDTO {
    private UUID eliteId;
    private String name;
    private Integer age;
    private Gender gender;
    private String email;
    private String mobileNumber;
    private String username;
    private RoleType role;
    private boolean active;
    private boolean emailVerified;
    private String address;
    private String emergencyContact;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
package com.eliteschool.auth_service.dto.common;

import com.eliteschool.auth_service.model.enums.Gender;
import com.eliteschool.auth_service.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseUserDTO {
    private String eliteId;
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "Username must be 3-20 characters long and can only contain letters, numbers, and underscores")
    private String username;

    private Integer age;
    private Gender gender;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    private RoleType role;
    private boolean active = true;
    private boolean emailVerified = false;
    private String address;
} 
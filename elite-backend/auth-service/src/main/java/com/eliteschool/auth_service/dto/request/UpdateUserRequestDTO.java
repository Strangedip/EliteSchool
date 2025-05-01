package com.eliteschool.auth_service.dto.request;

import com.eliteschool.auth_service.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequestDTO {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private Integer age;

    private Gender gender;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$", message = "Username must be 3-30 characters long and can only contain letters, numbers, and underscores")
    private String username;

    private String address;

    private String emergencyContact;
} 
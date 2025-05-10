package com.eliteschool.auth_service.dto.request;

import com.eliteschool.auth_service.dto.common.BaseUserDTO;
import com.eliteschool.auth_service.model.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequestDTO extends BaseUserDTO {
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;
} 
package com.eliteschool.auth_service.dto;

import com.eliteschool.auth_service.model.enums.Gender;
import com.eliteschool.auth_service.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID eliteId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private Integer age;
    private Gender gender;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;
    
    private RoleType role;
    private boolean active = true;
    private boolean emailVerified = false;
    
    // Common fields for all users
    private String address;
    private String emergencyContact;
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Authentication field (not for persistence)
    private String token;
    
    // Helper methods to check role-specific fields
    public boolean isStudent() {
        return RoleType.STUDENT.equals(role);
    }
    
    public boolean isFaculty() {
        return RoleType.FACULTY.equals(role);
    }
    
    public boolean isAdmin() {
        return RoleType.ADMIN.equals(role);
    }
} 
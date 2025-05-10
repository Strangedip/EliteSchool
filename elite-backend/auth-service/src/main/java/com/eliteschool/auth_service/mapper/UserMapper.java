package com.eliteschool.auth_service.mapper;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.dto.response.UserResponseDTO;
import com.eliteschool.auth_service.model.User;
import org.springframework.stereotype.Component;

public final class UserMapper {
    
    private UserMapper() {
        // Private constructor to prevent instantiation
    }

    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .eliteId(user.getEliteId())
                .name(user.getName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .address(user.getAddress())
                .emergencyContact(user.getEmergencyContact())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .eliteId(user.getEliteId())
                .name(user.getName())
                .age(user.getAge())
                .gender(user.getGender())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .address(user.getAddress())
                .emergencyContact(user.getEmergencyContact())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .eliteId(dto.getEliteId())
                .name(dto.getName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .mobileNumber(dto.getMobileNumber())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(dto.getRole())
                .active(dto.isActive())
                .emailVerified(dto.isEmailVerified())
                .address(dto.getAddress())
                .emergencyContact(dto.getEmergencyContact())
                .build();
    }

    public static User updateUserFromDTO(UserDTO dto, User existingUser) {
        if (dto == null || existingUser == null) {
            return existingUser;
        }

        if (dto.getName() != null) {
            existingUser.setName(dto.getName());
        }
        if (dto.getAge() != null) {
            existingUser.setAge(dto.getAge());
        }
        if (dto.getGender() != null) {
            existingUser.setGender(dto.getGender());
        }
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getMobileNumber() != null) {
            existingUser.setMobileNumber(dto.getMobileNumber());
        }
        if (dto.getUsername() != null) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            existingUser.setPassword(dto.getPassword());
        }
        if (dto.getRole() != null) {
            existingUser.setRole(dto.getRole());
        }
        existingUser.setActive(dto.isActive());
        existingUser.setEmailVerified(dto.isEmailVerified());
        if (dto.getAddress() != null) {
            existingUser.setAddress(dto.getAddress());
        }
        if (dto.getEmergencyContact() != null) {
            existingUser.setEmergencyContact(dto.getEmergencyContact());
        }

        return existingUser;
    }

    public static User updateUserFromRequestDTO(UpdateUserRequestDTO dto, User existingUser) {
        if (dto == null || existingUser == null) {
            return existingUser;
        }

        if (dto.getName() != null) {
            existingUser.setName(dto.getName());
        }
        if (dto.getAge() != null) {
            existingUser.setAge(dto.getAge());
        }
        if (dto.getGender() != null) {
            existingUser.setGender(dto.getGender());
        }
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getMobileNumber() != null) {
            existingUser.setMobileNumber(dto.getMobileNumber());
        }
        if (dto.getUsername() != null) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getAddress() != null) {
            existingUser.setAddress(dto.getAddress());
        }
        if (dto.getEmergencyContact() != null) {
            existingUser.setEmergencyContact(dto.getEmergencyContact());
        }

        return existingUser;
    }
} 
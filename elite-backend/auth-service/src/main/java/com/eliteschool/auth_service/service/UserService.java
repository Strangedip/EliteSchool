package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(User userDTO);
    User getUserById(UUID id);
    List<User> getAllUsers();
    User updateUser(UUID id, UpdateUserRequestDTO userDTO);
    void deleteUser(UUID id);
    User setActiveStatus(UUID id, boolean active);
    User updateRole(UUID id, RoleType role);
    User changePassword(UUID id, String encodedNewPassword);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User getUserEntityByUsername(String username);

    List<UserDTO> getAllStudents();

    List<UserDTO> getAllFaculty();
}

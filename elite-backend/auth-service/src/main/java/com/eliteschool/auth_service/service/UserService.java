package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // Common user operations
    User createUser(User userDTO);
    User getUserById(UUID id);
    List<User> getAllUsers();
    User updateUser(UUID id, UpdateUserRequestDTO userDTO);
    void deleteUser(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User getUserEntityByUsername(String username);

    // Student-specific operations

    List<UserDTO> getAllStudents();

    List<UserDTO> getAllFaculty();
}

package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.dto.response.UserResponseDTO;
import com.eliteschool.auth_service.mapper.UserMapper;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.service.UserService;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(u -> ResponseUtil.success("User found", UserMapper.toResponseDTO(u)))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                        "User not found with email: " + email, null));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(u -> ResponseUtil.success("User found", UserMapper.toResponseDTO(u)))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                        "User not found with username: " + username, null));
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<?> emailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseUtil.success("Email existence checked", exists);
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<?> usernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseUtil.success("Username existence checked", exists);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail()) || userService.existsByUsername(user.getUsername())) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, "USER_EXISTS",
                    "Email or Username already exists", "User creation failed");
        }
        User createdUser = userService.createUser(user);
        return ResponseUtil.success("User created successfully", UserMapper.toResponseDTO(createdUser));
    }

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        List<UserDTO> students = userService.getAllStudents();
        return ResponseUtil.success("All students retrieved", students);
    }

    @GetMapping("/faculty")
    public ResponseEntity<?> getAllFaculty() {
        List<UserDTO> faculty = userService.getAllFaculty();
        return ResponseUtil.success("All faculty retrieved", faculty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequestDTO userDTO) {
        try {
            User updatedUser = userService.updateUser(id, userDTO);
            return ResponseUtil.success("User updated successfully", UserMapper.toResponseDTO(updatedUser));
        } catch (RuntimeException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                    e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseUtil.success("User deleted successfully", null);
        } catch (RuntimeException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                    e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return ResponseUtil.success("User found", UserMapper.toResponseDTO(user));
        } catch (RuntimeException e) {
            return ResponseUtil.error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND",
                    e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> responseDTOs = users.stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseUtil.success("All users retrieved", responseDTOs);
    }
}

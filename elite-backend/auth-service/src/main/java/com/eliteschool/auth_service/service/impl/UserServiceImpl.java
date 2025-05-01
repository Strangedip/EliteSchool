package com.eliteschool.auth_service.service.impl;

import com.eliteschool.auth_service.dto.UserDTO;
import com.eliteschool.auth_service.dto.request.UpdateUserRequestDTO;
import com.eliteschool.auth_service.mapper.UserMapper;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;
import com.eliteschool.auth_service.repository.UserRepository;
import com.eliteschool.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UpdateUserRequestDTO userDTO) {
        User existingUser = getUserById(id);
        User updatedUser = UserMapper.updateUserFromRequestDTO(userDTO, existingUser);
        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserEntityByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public UserDTO getStudentByAdmissionNumber(String admissionNumber) {
        return userRepository.findByAdmissionNumberAndRole(admissionNumber, RoleType.STUDENT)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Student not found with admission number: " + admissionNumber));
    }

    @Override
    public List<UserDTO> getStudentsByGradeAndSection(String grade, String section) {
        return userRepository.findByGradeAndSectionAndRole(grade, section, RoleType.STUDENT)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllStudents() {
        return userRepository.findByRole(RoleType.STUDENT)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAdmissionNumber(String admissionNumber) {
        return userRepository.existsByAdmissionNumberAndRole(admissionNumber, RoleType.STUDENT);
    }

    @Override
    public UserDTO getFacultyByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeIdAndRole(employeeId, RoleType.FACULTY)
                .map(UserMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Faculty not found with employee id: " + employeeId));
    }

    @Override
    public List<UserDTO> getFacultyBySubject(String subject) {
        return userRepository.findBySubjectsContainingAndRole(subject, RoleType.FACULTY)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFacultyByGrade(String grade) {
        return userRepository.findByGradesContainingAndRole(grade, RoleType.FACULTY)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllFaculty() {
        return userRepository.findByRole(RoleType.FACULTY)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        return userRepository.existsByEmployeeIdAndRole(employeeId, RoleType.FACULTY);
    }
} 
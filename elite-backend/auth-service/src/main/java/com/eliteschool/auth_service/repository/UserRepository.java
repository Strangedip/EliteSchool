package com.eliteschool.auth_service.repository;

import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Find user by email (for login & authentication)
    Optional<User> findByEmail(String email);

    // Find user by username
    Optional<User> findByUsername(String username);

    // Find user by mobile number
    Optional<User> findByMobileNumber(String mobileNumber);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if mobile number already exists
    boolean existsByMobileNumber(String mobileNumber);

    // Student-specific queries
    Optional<User> findByAdmissionNumberAndRole(String admissionNumber, RoleType role);
    List<User> findByGradeAndSectionAndRole(String grade, String section, RoleType role);
    List<User> findByRole(RoleType role);
    boolean existsByAdmissionNumberAndRole(String admissionNumber, RoleType role);

    // Teacher-specific queries
    Optional<User> findByEmployeeIdAndRole(String employeeId, RoleType role);
    List<User> findBySubjectsContainingAndRole(String subject, RoleType role);
    List<User> findByGradesContainingAndRole(String grade, RoleType role);
    boolean existsByEmployeeIdAndRole(String employeeId, RoleType role);
}
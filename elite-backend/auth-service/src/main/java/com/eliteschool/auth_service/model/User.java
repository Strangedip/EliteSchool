package com.eliteschool.auth_service.model;

import com.eliteschool.auth_service.model.enums.Gender;
import com.eliteschool.auth_service.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Automatically generates UUID
    private UUID eliteId;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Enumerated(EnumType.STRING)  // Stores ENUM as String (MALE, FEMALE, OTHER)
    private Gender gender;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false) // Password should not be null
    private String password;

    @Enumerated(EnumType.STRING)  // Stores ENUM as String (STUDENT, FACULTY, etc.)
    @Column(nullable = false)
    private RoleType role;
}

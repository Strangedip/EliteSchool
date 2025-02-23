package com.school.elite.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    private String eliteId;  // Should be UUID or some unique identifier

    @Column(nullable = false)
    private String name;

    private Integer age;
    private String gender;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    private String position;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;
}

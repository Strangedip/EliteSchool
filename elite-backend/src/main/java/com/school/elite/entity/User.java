package com.school.elite.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elite_users")
public class User {
    @Id
    private String eliteId;
    private String name;
    private Integer age;
    private String gender;
    private String email;
    private String mobileNumber;
    private String position;
    private String username;
    private String password;
}

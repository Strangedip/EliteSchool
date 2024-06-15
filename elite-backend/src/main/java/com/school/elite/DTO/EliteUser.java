package com.school.elite.DTO;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elite_users")
public class EliteUser {
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

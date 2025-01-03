package com.school.elite.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequestDto {
    private String name;
    private Integer age;
    private String gender;
    private String email;
    private String mobileNumber;
    private String position;
    private String username;
    private String password;
}

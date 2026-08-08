package com.eliteschool.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {
    private UUID id;

    private String courseCode;

    @NotBlank(message = "Course name cannot be empty")
    private String name;

    private String description;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Grade is required")
    private String grade;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

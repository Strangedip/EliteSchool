package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    
    private UUID id;
    
    @NotBlank(message = "Task title is required")
    private String title;
    
    @NotBlank(message = "Task description is required")
    private String description;
    
    @NotNull(message = "Task type is required")
    private TaskType taskType;
    
    @NotNull(message = "Minimum level is required")
    @Min(value = 1, message = "Minimum level must be at least 1")
    private Integer minLevel;
    
    @NotNull(message = "Reward points are required")
    @Min(value = 1, message = "Reward points must be at least 1")
    private Integer rewardPoints;
    
    @NotNull(message = "Creator ID is required")
    private UUID createdBy;
    
    private TaskStatus status;
    
    private UUID completedBy;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime createdAt;
} 
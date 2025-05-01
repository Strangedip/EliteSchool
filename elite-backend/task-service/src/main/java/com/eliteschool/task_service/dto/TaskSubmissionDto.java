package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmissionDto {
    
    private UUID id;
    
    @NotNull(message = "Task ID is required")
    private UUID taskId;
    
    @NotNull(message = "Student ID is required")
    private UUID studentId;
    
    private String submissionDetails;
    
    private String evidence;
    
    private TaskStatus status;
    
    private String feedbackNotes;
    
    private UUID verifiedBy;
    
    private LocalDateTime submittedAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime verifiedAt;
} 
package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private UUID studentId;

    private String submissionDetails;

    private String evidence;

    @Builder.Default
    private List<String> rubricChecked = new ArrayList<>();

    private TaskStatus status;

    private String feedbackNotes;

    private UUID verifiedBy;

    private LocalDateTime submittedAt;

    private LocalDateTime updatedAt;

    private LocalDateTime verifiedAt;

    private Boolean pointsAwarded;

    private String taskTitle;

    private Integer rewardPoints;

    private String taskDescription;

    private UUID taskCreatedBy;

    private String taskType;

    private Boolean evidenceRequired;

    private Integer minNotesLength;

    private List<String> rubricChecklist;
}

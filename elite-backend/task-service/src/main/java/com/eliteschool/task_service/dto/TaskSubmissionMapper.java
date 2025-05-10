package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.TaskSubmission;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between TaskSubmission entity and TaskSubmissionDto.
 */
public class TaskSubmissionMapper {

    /**
     * Convert from TaskSubmission entity to TaskSubmissionDto.
     * @param submission The TaskSubmission entity.
     * @return TaskSubmissionDto.
     */
    public static TaskSubmissionDto toDto(TaskSubmission submission) {
        if (submission == null) {
            return null;
        }
        
        return TaskSubmissionDto.builder()
                .id(submission.getId())
                .taskId(submission.getTaskId())
                .studentId(submission.getStudentId())
                .submissionDetails(submission.getSubmissionDetails())
                .evidence(submission.getEvidence())
                .status(submission.getStatus())
                .feedbackNotes(submission.getFeedbackNotes())
                .verifiedBy(submission.getVerifiedBy())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .verifiedAt(submission.getVerifiedAt())
                .build();
    }

    /**
     * Convert from TaskSubmissionDto to TaskSubmission entity.
     * @param dto The TaskSubmissionDto.
     * @return TaskSubmission entity.
     */
    public static TaskSubmission toEntity(TaskSubmissionDto dto) {
        if (dto == null) {
            return null;
        }
        
        return TaskSubmission.builder()
                .id(dto.getId())
                .taskId(dto.getTaskId())
                .studentId(dto.getStudentId())
                .submissionDetails(dto.getSubmissionDetails())
                .evidence(dto.getEvidence())
                .status(dto.getStatus())
                .feedbackNotes(dto.getFeedbackNotes())
                .verifiedBy(dto.getVerifiedBy())
                .submittedAt(dto.getSubmittedAt())
                .updatedAt(dto.getUpdatedAt())
                .verifiedAt(dto.getVerifiedAt())
                .build();
    }

    /**
     * Convert a list of TaskSubmission entities to a list of TaskSubmissionDtos.
     * @param submissions List of TaskSubmission entities.
     * @return List of TaskSubmissionDtos.
     */
    public static List<TaskSubmissionDto> toDtoList(List<TaskSubmission> submissions) {
        if (submissions == null) {
            return List.of();
        }
        
        return submissions.stream()
                .map(TaskSubmissionMapper::toDto)
                .collect(Collectors.toList());
    }
} 
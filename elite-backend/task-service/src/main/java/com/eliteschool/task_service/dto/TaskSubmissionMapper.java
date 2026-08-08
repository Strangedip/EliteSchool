package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.TaskSubmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class TaskSubmissionMapper {

    private TaskSubmissionMapper() {}

    public static TaskSubmissionDto toDto(TaskSubmission submission) {
        return toDto(submission, null);
    }

    public static TaskSubmissionDto toDto(TaskSubmission submission, Task task) {
        if (submission == null) {
            return null;
        }

        TaskSubmissionDto.TaskSubmissionDtoBuilder builder = TaskSubmissionDto.builder()
                .id(submission.getId())
                .taskId(submission.getTaskId())
                .studentId(submission.getStudentId())
                .submissionDetails(submission.getSubmissionDetails())
                .evidence(submission.getEvidence())
                .rubricChecked(submission.getRubricChecked() != null
                        ? new ArrayList<>(submission.getRubricChecked())
                        : new ArrayList<>())
                .status(submission.getStatus())
                .feedbackNotes(submission.getFeedbackNotes())
                .verifiedBy(submission.getVerifiedBy())
                .submittedAt(submission.getSubmittedAt())
                .updatedAt(submission.getUpdatedAt())
                .verifiedAt(submission.getVerifiedAt())
                .pointsAwarded(submission.isPointsAwarded());

        if (task != null) {
            builder.taskTitle(task.getTitle())
                    .taskDescription(task.getDescription())
                    .rewardPoints(task.getRewardPoints())
                    .taskCreatedBy(task.getCreatedBy())
                    .taskType(task.getTaskType() != null ? task.getTaskType().name() : null)
                    .evidenceRequired(task.isEvidenceRequired())
                    .minNotesLength(task.getMinNotesLength())
                    .rubricChecklist(task.getRubricChecklist() != null
                            ? new ArrayList<>(task.getRubricChecklist())
                            : new ArrayList<>());
        }

        return builder.build();
    }

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
                .rubricChecked(dto.getRubricChecked() != null
                        ? new ArrayList<>(dto.getRubricChecked())
                        : new ArrayList<>())
                .status(dto.getStatus())
                .feedbackNotes(dto.getFeedbackNotes())
                .verifiedBy(dto.getVerifiedBy())
                .submittedAt(dto.getSubmittedAt())
                .updatedAt(dto.getUpdatedAt())
                .verifiedAt(dto.getVerifiedAt())
                .build();
    }

    public static List<TaskSubmissionDto> toDtoList(List<TaskSubmission> submissions, Map<UUID, Task> tasksById) {
        if (submissions == null) {
            return List.of();
        }

        return submissions.stream()
                .map(s -> toDto(s, tasksById != null ? tasksById.get(s.getTaskId()) : null))
                .collect(Collectors.toList());
    }
}

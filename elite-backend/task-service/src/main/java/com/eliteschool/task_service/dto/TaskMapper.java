package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .taskType(task.getTaskType())
                .minLevel(task.getMinLevel())
                .rewardPoints(task.getRewardPoints())
                .createdBy(task.getCreatedBy())
                .status(task.getStatus())
                .completedBy(task.getCompletedBy())
                .completedAt(task.getCompletedAt())
                .evidenceRequired(task.isEvidenceRequired())
                .minNotesLength(task.getMinNotesLength())
                .rubricChecklist(task.getRubricChecklist() != null
                        ? new ArrayList<>(task.getRubricChecklist())
                        : new ArrayList<>())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public static Task toEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }

        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .taskType(dto.getTaskType())
                .minLevel(dto.getMinLevel())
                .rewardPoints(dto.getRewardPoints())
                .createdBy(dto.getCreatedBy())
                .status(dto.getStatus())
                .completedBy(dto.getCompletedBy())
                .completedAt(dto.getCompletedAt())
                .evidenceRequired(dto.getEvidenceRequired() == null || dto.getEvidenceRequired())
                .minNotesLength(dto.getMinNotesLength() != null ? dto.getMinNotesLength() : 40)
                .rubricChecklist(dto.getRubricChecklist() != null
                        ? new ArrayList<>(dto.getRubricChecklist())
                        : new ArrayList<>())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static List<TaskDto> toDtoList(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }

        return tasks.stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }
}

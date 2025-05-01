package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.Task;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Task entity and TaskDto.
 */
public class TaskMapper {

    /**
     * Convert from Task entity to TaskDto.
     * @param task The Task entity.
     * @return TaskDto.
     */
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
                .createdAt(task.getCreatedAt())
                .build();
    }

    /**
     * Convert from TaskDto to Task entity.
     * @param dto The TaskDto.
     * @return Task entity.
     */
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
                .createdAt(dto.getCreatedAt())
                .build();
    }

    /**
     * Convert a list of Task entities to a list of TaskDtos.
     * @param tasks List of Task entities.
     * @return List of TaskDtos.
     */
    public static List<TaskDto> toDtoList(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }
        
        return tasks.stream()
                .map(TaskMapper::toDto)
                .collect(Collectors.toList());
    }
} 
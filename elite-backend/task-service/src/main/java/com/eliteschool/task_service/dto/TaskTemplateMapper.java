package com.eliteschool.task_service.dto;

import com.eliteschool.task_service.model.TaskTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskTemplateMapper {

    public static TaskTemplateDto toDto(TaskTemplate template) {
        if (template == null) {
            return null;
        }

        return TaskTemplateDto.builder()
                .id(template.getId())
                .title(template.getTitle())
                .description(template.getDescription())
                .taskType(template.getTaskType())
                .minLevel(template.getMinLevel())
                .rewardPoints(template.getRewardPoints())
                .evidenceRequired(template.isEvidenceRequired())
                .minNotesLength(template.getMinNotesLength())
                .rubricChecklist(template.getRubricChecklist() != null
                        ? new ArrayList<>(template.getRubricChecklist())
                        : new ArrayList<>())
                .createdBy(template.getCreatedBy())
                .createdAt(template.getCreatedAt())
                .build();
    }

    public static TaskTemplate toEntity(TaskTemplateDto dto) {
        if (dto == null) {
            return null;
        }

        return TaskTemplate.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .taskType(dto.getTaskType())
                .minLevel(dto.getMinLevel())
                .rewardPoints(dto.getRewardPoints())
                .evidenceRequired(dto.getEvidenceRequired() == null || dto.getEvidenceRequired())
                .minNotesLength(dto.getMinNotesLength() != null ? dto.getMinNotesLength() : 40)
                .rubricChecklist(dto.getRubricChecklist() != null
                        ? new ArrayList<>(dto.getRubricChecklist())
                        : new ArrayList<>())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public static List<TaskTemplateDto> toDtoList(List<TaskTemplate> templates) {
        if (templates == null) {
            return List.of();
        }

        return templates.stream()
                .map(TaskTemplateMapper::toDto)
                .collect(Collectors.toList());
    }
}

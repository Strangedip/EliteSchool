package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskTemplateDto;
import com.eliteschool.task_service.dto.TaskTemplateMapper;
import com.eliteschool.task_service.model.TaskTemplate;
import com.eliteschool.task_service.repository.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskTemplateService {

    private final TaskTemplateRepository taskTemplateRepository;

    @Transactional
    public TaskTemplateDto createTemplate(TaskTemplateDto dto) {
        log.info("Creating task template: {}", dto.getTitle());
        TaskTemplate template = TaskTemplateMapper.toEntity(dto);
        return TaskTemplateMapper.toDto(taskTemplateRepository.save(template));
    }

    public List<TaskTemplateDto> getAllTemplates() {
        return TaskTemplateMapper.toDtoList(taskTemplateRepository.findAll());
    }

    public Optional<TaskTemplateDto> getTemplateById(UUID templateId) {
        return taskTemplateRepository.findById(templateId).map(TaskTemplateMapper::toDto);
    }

    @Transactional
    public Optional<TaskTemplateDto> updateTemplate(UUID templateId, TaskTemplateDto dto) {
        return taskTemplateRepository.findById(templateId).map(template -> {
            template.setTitle(dto.getTitle());
            template.setDescription(dto.getDescription());
            template.setTaskType(dto.getTaskType());
            template.setMinLevel(dto.getMinLevel());
            template.setRewardPoints(dto.getRewardPoints());
            if (dto.getEvidenceRequired() != null) {
                template.setEvidenceRequired(dto.getEvidenceRequired());
            }
            if (dto.getMinNotesLength() != null) {
                template.setMinNotesLength(Math.max(0, dto.getMinNotesLength()));
            }
            if (dto.getRubricChecklist() != null) {
                template.setRubricChecklist(new java.util.ArrayList<>(dto.getRubricChecklist()));
            }
            return TaskTemplateMapper.toDto(taskTemplateRepository.save(template));
        });
    }

    @Transactional
    public boolean deleteTemplate(UUID templateId) {
        if (taskTemplateRepository.existsById(templateId)) {
            taskTemplateRepository.deleteById(templateId);
            return true;
        }
        return false;
    }
}

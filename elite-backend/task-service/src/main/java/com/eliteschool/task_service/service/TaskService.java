package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.dto.TaskMapper;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.repository.TaskRepository;
import com.eliteschool.task_service.repository.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskTemplateRepository taskTemplateRepository;

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Creating task: {}", taskDto.getTitle());
        Task task = TaskMapper.toEntity(taskDto);
        task.setStatus(TaskStatus.OPEN);
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public Optional<TaskDto> createTaskFromTemplate(UUID templateId, UUID createdBy) {
        return taskTemplateRepository.findById(templateId).map(template -> {
            log.info("Creating task from template {}: {}", templateId, template.getTitle());
            Task task = Task.builder()
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
                    .createdBy(createdBy)
                    .status(TaskStatus.OPEN)
                    .build();
            return TaskMapper.toDto(taskRepository.save(task));
        });
    }

    public List<TaskDto> getAllTask() {
        return TaskMapper.toDtoList(taskRepository.findAll());
    }

    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        return TaskMapper.toDtoList(taskRepository.findByStatus(status));
    }

    public List<TaskDto> getTasksByCreator(UUID createdBy) {
        return TaskMapper.toDtoList(taskRepository.findByCreatedBy(createdBy));
    }

    public Optional<TaskDto> getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).map(TaskMapper::toDto);
    }

    @Transactional
    public Optional<TaskDto> closeTask(UUID taskId) {
        return taskRepository.findById(taskId).map(task -> {
            task.setStatus(TaskStatus.CLOSED);
            return TaskMapper.toDto(taskRepository.save(task));
        });
    }

    @Transactional
    public Optional<TaskDto> updateTask(UUID taskId, TaskDto taskDto) {
        return taskRepository.findById(taskId).map(task -> {
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setTaskType(taskDto.getTaskType());
            task.setMinLevel(taskDto.getMinLevel());
            task.setRewardPoints(taskDto.getRewardPoints());
            if (taskDto.getEvidenceRequired() != null) {
                task.setEvidenceRequired(taskDto.getEvidenceRequired());
            }
            if (taskDto.getMinNotesLength() != null) {
                task.setMinNotesLength(Math.max(0, taskDto.getMinNotesLength()));
            }
            if (taskDto.getRubricChecklist() != null) {
                task.setRubricChecklist(new ArrayList<>(taskDto.getRubricChecklist()));
            }
            return TaskMapper.toDto(taskRepository.save(task));
        });
    }

    @Transactional
    public boolean deleteTask(UUID taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }
}

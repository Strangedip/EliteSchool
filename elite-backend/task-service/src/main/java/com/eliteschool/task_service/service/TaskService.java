package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.dto.TaskMapper;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Creating task: {}", taskDto.getTitle());
        Task task = TaskMapper.toEntity(taskDto);
        task.setStatus(TaskStatus.OPEN);
        return TaskMapper.toDto(taskRepository.save(task));
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

    // For SINGLE tasks only - MULTIPLE tasks use submissions
    @Transactional
    public Optional<TaskDto> completeTask(UUID taskId, UUID completedBy) {
        return taskRepository.findById(taskId).map(task -> {
            if (task.getTaskType() == TaskType.SINGLE) {
                task.setCompletedBy(completedBy);
                task.setStatus(TaskStatus.COMPLETED);
                task.setCompletedAt(LocalDateTime.now());
                return TaskMapper.toDto(taskRepository.save(task));
            }
            // MULTIPLE type tasks are completed via task-submissions
            return TaskMapper.toDto(task);
        });
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

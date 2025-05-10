package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.dto.TaskMapper;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Create a new task.
     * @param taskDto The task to save.
     * @return TaskDto (saved entity)
     */
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        log.info("Creating new task with title: {}", taskDto.getTitle());
        Task task = TaskMapper.toEntity(taskDto);
        task.setStatus(TaskStatus.OPEN); // New tasks start as OPEN
        Task savedTask = taskRepository.save(task);
        log.info("Task created with ID: {}", savedTask.getId());
        return TaskMapper.toDto(savedTask);
    }

    /**
     * Get all tasks.
     * @return List of tasks.
     */
    public List<TaskDto> getAllTask() {
        List<Task> tasks = taskRepository.findAll();
        return TaskMapper.toDtoList(tasks);
    }

    /**
     * Get all tasks by status.
     * @param status The task status (OPEN, COMPLETED, CLOSED).
     * @return List of tasks.
     */
    public List<TaskDto> getTasksByStatus(TaskStatus status) {
        log.info("Fetching tasks with status: {}", status);
        List<Task> tasks = taskRepository.findByStatus(status);
        log.info("Found {} tasks with status: {}", tasks.size(), status);
        return TaskMapper.toDtoList(tasks);
    }

    /**
     * Get all tasks created by a specific faculty/management user.
     * @param createdBy The creator's ID.
     * @return List of tasks.
     */
    public List<TaskDto> getTasksByCreator(UUID createdBy) {
        log.info("Fetching tasks created by user with ID: {}", createdBy);
        List<Task> tasks = taskRepository.findByCreatedBy(createdBy);
        log.info("Found {} tasks created by user with ID: {}", tasks.size(), createdBy);
        return TaskMapper.toDtoList(tasks);
    }

    /**
     * Find a task by its ID.
     * @param taskId The ID of the task.
     * @return Optional<TaskDto>
     */
    public Optional<TaskDto> getTaskById(UUID taskId) {
        log.info("Fetching task with ID: {}", taskId);
        return taskRepository.findById(taskId)
                .map(task -> {
                    log.info("Found task with ID: {}", taskId);
                    return TaskMapper.toDto(task);
                });
    }

    /**
     * Mark a task as completed.
     * @param taskId The ID of the task to complete.
     * @param completedBy The student who completed the task.
     * @return The updated task.
     */
    @Transactional
    public Optional<TaskDto> completeTask(UUID taskId, UUID completedBy) {
        log.info("Completing task with ID: {} by student: {}", taskId, completedBy);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            if (task.getTaskType() == TaskType.SINGLE) {
                task.setCompletedBy(completedBy);
                task.setStatus(TaskStatus.COMPLETED);
                task.setCompletedAt(LocalDateTime.now());
                Task updatedTask = taskRepository.save(task);
                log.info("Task with ID: {} marked as completed", taskId);
                return Optional.of(TaskMapper.toDto(updatedTask));
            } else {
                // MULTIPLE task logic (handled separately via submissions table)
                log.info("Task is of type MULTIPLE, completion handled via submissions");
                return Optional.of(TaskMapper.toDto(task));
            }
        }
        log.warn("Task with ID: {} not found", taskId);
        return Optional.empty();
    }

    /**
     * Close a task.
     * @param taskId The ID of the task to close.
     * @return The updated task.
     */
    @Transactional
    public Optional<TaskDto> closeTask(UUID taskId) {
        log.info("Closing task with ID: {}", taskId);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(TaskStatus.CLOSED);
            Task updatedTask = taskRepository.save(task);
            log.info("Task with ID: {} closed successfully", taskId);
            return Optional.of(TaskMapper.toDto(updatedTask));
        }
        log.warn("Task with ID: {} not found", taskId);
        return Optional.empty();
    }
}

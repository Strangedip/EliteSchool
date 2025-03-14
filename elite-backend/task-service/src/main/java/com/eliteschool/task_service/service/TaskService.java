package com.eliteschool.task_service.service;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Create a new task.
     * @param task The task entity to save.
     * @return Task (saved entity)
     */
    public Task createTask(Task task) {
        task.setStatus(TaskStatus.OPEN); // New tasks start as OPEN
        return taskRepository.save(task);
    }

    /**
     * Get all tasks by status.
     * @param status The task status (OPEN, COMPLETED, CLOSED).
     * @return List of tasks.
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Get all tasks created by a specific faculty/management user.
     * @param createdBy The creator's ID.
     * @return List of tasks.
     */
    public List<Task> getTasksByCreator(UUID createdBy) {
        return taskRepository.findByCreatedBy(createdBy);
    }

    /**
     * Find a task by its ID.
     * @param taskId The ID of the task.
     * @return Optional<Task>
     */
    public Optional<Task> getTaskById(UUID taskId) {
        return taskRepository.findById(taskId);
    }

    /**
     * Mark a task as completed.
     * @param taskId The ID of the task to complete.
     * @param completedBy The student who completed the task.
     * @return The updated task.
     */
    public Optional<Task> completeTask(UUID taskId, UUID completedBy) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            if (task.getTaskType() == TaskType.SINGLE) {
                task.setCompletedBy(completedBy);
                task.setStatus(TaskStatus.COMPLETED);
                task.setCompletedAt(LocalDateTime.now());
                return Optional.of(taskRepository.save(task));
            } else {
                // MULTIPLE task logic (handled separately via submissions table)
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    /**
     * Close a task.
     * @param taskId The ID of the task to close.
     * @return The updated task.
     */
    public Optional<Task> closeTask(UUID taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(TaskStatus.CLOSED);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }
}

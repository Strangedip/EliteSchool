package com.eliteschool.task_service.repository;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Get all tasks by status (OPEN, COMPLETED, CLOSED)
    List<Task> findByStatus(TaskStatus status);

    // Get all tasks created by a specific faculty/management user
    List<Task> findByCreatedBy(UUID createdBy);

    // Get a specific task by ID
    Optional<Task> findById(UUID id);
}

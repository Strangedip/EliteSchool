package com.eliteschool.task_service.repository;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByCreatedBy(UUID createdBy);

    Optional<Task> findById(UUID id);
}

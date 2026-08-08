package com.eliteschool.task_service.repository;

import com.eliteschool.task_service.model.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, UUID> {

    List<TaskTemplate> findByCreatedBy(UUID createdBy);
}

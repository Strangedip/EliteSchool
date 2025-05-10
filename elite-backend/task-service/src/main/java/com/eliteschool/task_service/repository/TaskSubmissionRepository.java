package com.eliteschool.task_service.repository;

import com.eliteschool.task_service.model.TaskSubmission;
import com.eliteschool.task_service.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, UUID> {
    
    List<TaskSubmission> findByStudentId(UUID studentId);
    
    List<TaskSubmission> findByTaskId(UUID taskId);
    
    List<TaskSubmission> findByStatus(TaskStatus status);
    
    Optional<TaskSubmission> findByTaskIdAndStudentId(UUID taskId, UUID studentId);
    
    List<TaskSubmission> findByStudentIdAndStatus(UUID studentId, TaskStatus status);
} 
package com.eliteschool.task_service.service;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.TaskSubmission;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.repository.TaskRepository;
import com.eliteschool.task_service.repository.TaskSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskSubmissionService {

    private final TaskSubmissionRepository taskSubmissionRepository;
    private final TaskRepository taskRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TaskSubmissionService(
            TaskSubmissionRepository taskSubmissionRepository,
            TaskRepository taskRepository,
            WebClient.Builder webClientBuilder) {
        this.taskSubmissionRepository = taskSubmissionRepository;
        this.taskRepository = taskRepository;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Submit a task for verification
     */
    public TaskSubmission submitTask(TaskSubmission submission) {
        submission.setStatus(TaskStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        return taskSubmissionRepository.save(submission);
    }

    /**
     * Verify a task submission (approve or reject)
     */
    @Transactional
    public Optional<TaskSubmission> verifySubmission(UUID submissionId, UUID verifierId, boolean approved, String feedback) {
        Optional<TaskSubmission> submissionOpt = taskSubmissionRepository.findById(submissionId);
        
        if (submissionOpt.isPresent()) {
            TaskSubmission submission = submissionOpt.get();
            
            // Update submission status
            submission.setStatus(approved ? TaskStatus.COMPLETED : TaskStatus.REJECTED);
            submission.setVerifiedBy(verifierId);
            submission.setVerifiedAt(LocalDateTime.now());
            submission.setFeedbackNotes(feedback);
            
            // If approved, also update the task if needed and award points
            if (approved) {
                // Award points through reward service
                awardPoints(submission.getStudentId(), submission.getTaskId());
            }
            
            return Optional.of(taskSubmissionRepository.save(submission));
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all submissions for a specific task
     */
    public List<TaskSubmission> getSubmissionsByTask(UUID taskId) {
        return taskSubmissionRepository.findByTaskId(taskId);
    }
    
    /**
     * Get all submissions by a specific student
     */
    public List<TaskSubmission> getSubmissionsByStudent(UUID studentId) {
        return taskSubmissionRepository.findByStudentId(studentId);
    }
    
    /**
     * Get all submissions with a specific status
     */
    public List<TaskSubmission> getSubmissionsByStatus(TaskStatus status) {
        return taskSubmissionRepository.findByStatus(status);
    }
    
    /**
     * Award points to a student for completing a task
     */
    private void awardPoints(UUID studentId, UUID taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            int pointsToAward = task.getRewardPoints();
            
            // Call reward service to add points to student's wallet
            webClientBuilder.build()
                .post()
                .uri("http://reward-service/api/rewards/award")
                .bodyValue(new RewardRequest(studentId, pointsToAward, "Task completion: " + task.getTitle()))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
        }
    }
    
    // Simple DTO for reward service request
    private record RewardRequest(UUID studentId, int points, String description) {}
} 
package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskSubmissionDto;
import com.eliteschool.task_service.dto.TaskSubmissionMapper;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.TaskSubmission;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.repository.TaskRepository;
import com.eliteschool.task_service.repository.TaskSubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
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
     * @param submissionDto The task submission data
     * @return The saved submission DTO
     */
    @Transactional
    public TaskSubmissionDto submitTask(TaskSubmissionDto submissionDto) {
        log.info("Submitting task with ID: {} by student: {}", 
                submissionDto.getTaskId(), submissionDto.getStudentId());
        
        TaskSubmission submission = TaskSubmissionMapper.toEntity(submissionDto);
        submission.setStatus(TaskStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        
        TaskSubmission savedSubmission = taskSubmissionRepository.save(submission);
        log.info("Task submission created with ID: {}", savedSubmission.getId());
        
        return TaskSubmissionMapper.toDto(savedSubmission);
    }

    /**
     * Verify a task submission (approve or reject)
     * @param submissionId The submission ID
     * @param verifierId The verifier's ID
     * @param approved Whether the submission is approved
     * @param feedback Feedback notes
     * @return The updated submission DTO
     */
    @Transactional
    public Optional<TaskSubmissionDto> verifySubmission(UUID submissionId, UUID verifierId, boolean approved, String feedback) {
        log.info("Verifying submission ID: {} by verifier: {}, approved: {}", 
                submissionId, verifierId, approved);
        
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
                log.info("Points awarded to student: {} for task: {}", 
                        submission.getStudentId(), submission.getTaskId());
            }
            
            TaskSubmission updatedSubmission = taskSubmissionRepository.save(submission);
            log.info("Submission ID: {} updated with status: {}", 
                    submissionId, updatedSubmission.getStatus());
            
            return Optional.of(TaskSubmissionMapper.toDto(updatedSubmission));
        }
        
        log.warn("Submission with ID: {} not found", submissionId);
        return Optional.empty();
    }
    
    /**
     * Get all submissions for a specific task
     * @param taskId The task ID
     * @return List of submission DTOs
     */
    public List<TaskSubmissionDto> getSubmissionsByTask(UUID taskId) {
        log.info("Fetching submissions for task with ID: {}", taskId);
        List<TaskSubmission> submissions = taskSubmissionRepository.findByTaskId(taskId);
        log.info("Found {} submissions for task with ID: {}", submissions.size(), taskId);
        return TaskSubmissionMapper.toDtoList(submissions);
    }
    
    /**
     * Get all submissions by a specific student
     * @param studentId The student ID
     * @return List of submission DTOs
     */
    public List<TaskSubmissionDto> getSubmissionsByStudent(UUID studentId) {
        log.info("Fetching submissions for student with ID: {}", studentId);
        List<TaskSubmission> submissions = taskSubmissionRepository.findByStudentId(studentId);
        log.info("Found {} submissions for student with ID: {}", submissions.size(), studentId);
        return TaskSubmissionMapper.toDtoList(submissions);
    }
    
    /**
     * Get all submissions with a specific status
     * @param status The submission status
     * @return List of submission DTOs
     */
    public List<TaskSubmissionDto> getSubmissionsByStatus(TaskStatus status) {
        log.info("Fetching submissions with status: {}", status);
        List<TaskSubmission> submissions = taskSubmissionRepository.findByStatus(status);
        log.info("Found {} submissions with status: {}", submissions.size(), status);
        return TaskSubmissionMapper.toDtoList(submissions);
    }
    
    /**
     * Award points to a student for completing a task
     * @param studentId The student ID
     * @param taskId The task ID
     */
    private void awardPoints(UUID studentId, UUID taskId) {
        log.info("Awarding points to student: {} for task: {}", studentId, taskId);
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            int pointsToAward = task.getRewardPoints();
            
            // Call reward service to add points to student's wallet
            try {
                webClientBuilder.build()
                    .post()
                    .uri("http://reward-service/api/rewards/award")
                    .bodyValue(new RewardRequest(studentId, pointsToAward, "Task completion: " + task.getTitle()))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .subscribe();
                
                log.info("Reward service called to award {} points to student: {}", 
                        pointsToAward, studentId);
            } catch (Exception e) {
                log.error("Error calling reward service: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Task with ID: {} not found for awarding points", taskId);
        }
    }
    
    // Simple DTO for reward service request
    private record RewardRequest(UUID studentId, int points, String description) {}
} 
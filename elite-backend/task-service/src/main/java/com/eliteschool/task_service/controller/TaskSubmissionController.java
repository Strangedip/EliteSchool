package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.TaskSubmissionDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskSubmissionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-submissions")
@Slf4j
public class TaskSubmissionController {

    private final TaskSubmissionService taskSubmissionService;

    @Autowired
    public TaskSubmissionController(TaskSubmissionService taskSubmissionService) {
        this.taskSubmissionService = taskSubmissionService;
    }

    /**
     * Submit a task for verification.
     * @param submissionDto The submission details.
     * @return ResponseEntity with created submission.
     */
    @PostMapping
    public ResponseEntity<CommonResponseDto<TaskSubmissionDto>> submitTask(@Valid @RequestBody TaskSubmissionDto submissionDto) {
        log.info("Received request to submit task with ID: {} by student: {}", 
                submissionDto.getTaskId(), submissionDto.getStudentId());
        
        TaskSubmissionDto savedSubmission = taskSubmissionService.submitTask(submissionDto);
        return ResponseUtil.success("Task submitted successfully", savedSubmission);
    }

    /**
     * Get all submissions for a specific task.
     * @param taskId The task ID.
     * @return ResponseEntity with list of submissions.
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByTask(@PathVariable UUID taskId) {
        log.info("Received request to get submissions for task with ID: {}", taskId);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByTask(taskId);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    /**
     * Get all submissions by a specific student.
     * @param studentId The student ID.
     * @return ResponseEntity with list of submissions.
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByStudent(@PathVariable UUID studentId) {
        log.info("Received request to get submissions for student with ID: {}", studentId);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByStudent(studentId);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    /**
     * Get all submissions with a specific status.
     * @param status The submission status.
     * @return ResponseEntity with list of submissions.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByStatus(@PathVariable TaskStatus status) {
        log.info("Received request to get submissions with status: {}", status);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByStatus(status);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    /**
     * Verify a task submission (approve or reject).
     * @param submissionId The submission ID.
     * @param verifierId The verifier's ID.
     * @param approved Whether the submission is approved.
     * @param feedback Feedback notes.
     * @return ResponseEntity with updated submission.
     */
    @PutMapping("/{submissionId}/verify")
    public ResponseEntity<CommonResponseDto<TaskSubmissionDto>> verifySubmission(
            @PathVariable UUID submissionId,
            @RequestParam UUID verifierId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String feedback) {
        
        log.info("Received request to verify submission with ID: {} by verifier: {}, approved: {}", 
                submissionId, verifierId, approved);
        
        return taskSubmissionService.verifySubmission(submissionId, verifierId, approved, feedback)
                .map(submission -> ResponseUtil.success(
                        approved ? "Submission approved successfully" : "Submission rejected",
                        submission
                ))
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.NOT_FOUND,
                        "SUBMISSION_NOT_FOUND",
                        "Submission with ID " + submissionId + " not found",
                        "Submission not found"
                ));
    }
} 
package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.CompletionCheckRequest;
import com.eliteschool.task_service.dto.CompletionCheckResponse;
import com.eliteschool.task_service.dto.TaskSubmissionDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskSubmissionService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping
    public ResponseEntity<CommonResponseDto<TaskSubmissionDto>> submitTask(
            @Valid @RequestBody TaskSubmissionDto submissionDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "STUDENT");
        UUID studentId = GatewayAuth.requireUserId(request);
        submissionDto.setStudentId(studentId);

        log.info("Received request to submit task with ID: {} by student: {}",
                submissionDto.getTaskId(), studentId);

        TaskSubmissionDto savedSubmission = taskSubmissionService.submitTask(submissionDto);
        return ResponseUtil.success("Task submitted successfully", savedSubmission);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByTask(
            @PathVariable UUID taskId,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        log.info("Received request to get submissions for task with ID: {}", taskId);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByTask(taskId);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByStudent(
            @PathVariable UUID studentId,
            HttpServletRequest request) {
        GatewayAuth.requireSelfOrRoles(request, studentId, "FACULTY", "ADMIN", "MANAGEMENT");
        log.info("Received request to get submissions for student with ID: {}", studentId);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByStudent(studentId);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<List<TaskSubmissionDto>>> getSubmissionsByStatus(
            @PathVariable TaskStatus status,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        log.info("Received request to get submissions with status: {}", status);
        List<TaskSubmissionDto> submissions = taskSubmissionService.getSubmissionsByStatus(status);
        return ResponseUtil.success("Submissions retrieved successfully", submissions);
    }

    @PutMapping("/{submissionId}/verify")
    public ResponseEntity<CommonResponseDto<TaskSubmissionDto>> verifySubmission(
            @PathVariable UUID submissionId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String feedback,
            HttpServletRequest request) {

        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        UUID verifierId = GatewayAuth.requireUserId(request);

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

    @PostMapping("/completion-check")
    public ResponseEntity<CommonResponseDto<CompletionCheckResponse>> checkCompletion(
            @RequestBody CompletionCheckRequest body,
            HttpServletRequest request) {
        GatewayAuth.requireInternal(request);
        CompletionCheckResponse result = taskSubmissionService.checkCompletion(
                body.getStudentId(), body.getTaskIds());
        return ResponseUtil.success("Completion checked", result);
    }
}

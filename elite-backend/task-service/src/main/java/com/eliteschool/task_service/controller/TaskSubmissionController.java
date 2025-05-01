package com.eliteschool.task_service.controller;

import com.eliteschool.task_service.model.TaskSubmission;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/task-submissions")
public class TaskSubmissionController {

    private final TaskSubmissionService taskSubmissionService;

    @Autowired
    public TaskSubmissionController(TaskSubmissionService taskSubmissionService) {
        this.taskSubmissionService = taskSubmissionService;
    }

    @PostMapping
    public ResponseEntity<TaskSubmission> submitTask(@RequestBody TaskSubmission submission) {
        TaskSubmission savedSubmission = taskSubmissionService.submitTask(submission);
        return new ResponseEntity<>(savedSubmission, HttpStatus.CREATED);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskSubmission>> getSubmissionsByTask(@PathVariable UUID taskId) {
        List<TaskSubmission> submissions = taskSubmissionService.getSubmissionsByTask(taskId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<TaskSubmission>> getSubmissionsByStudent(@PathVariable UUID studentId) {
        List<TaskSubmission> submissions = taskSubmissionService.getSubmissionsByStudent(studentId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskSubmission>> getSubmissionsByStatus(@PathVariable TaskStatus status) {
        List<TaskSubmission> submissions = taskSubmissionService.getSubmissionsByStatus(status);
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/{submissionId}/verify")
    public ResponseEntity<TaskSubmission> verifySubmission(
            @PathVariable UUID submissionId,
            @RequestParam UUID verifierId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String feedback) {
        
        Optional<TaskSubmission> verifiedSubmission = 
            taskSubmissionService.verifySubmission(submissionId, verifierId, approved, feedback);
        
        return verifiedSubmission
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 
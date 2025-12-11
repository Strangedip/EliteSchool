package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getAllTasks() {
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getAllTask());
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<TaskDto>> createTask(@Valid @RequestBody TaskDto taskDto) {
        log.info("Creating task: {}", taskDto.getTitle());
        return ResponseUtil.success("Task created successfully", taskService.createTask(taskDto));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getTasksByStatus(status));
    }

    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByCreator(@PathVariable UUID createdBy) {
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getTasksByCreator(createdBy));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> getTaskById(@PathVariable UUID taskId) {
        return taskService.getTaskById(taskId)
                .map(task -> ResponseUtil.success("Task retrieved successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    // Marks a SINGLE-type task as completed by a student
    @PutMapping("/{taskId}/complete/{completedBy}")
    public ResponseEntity<CommonResponseDto<TaskDto>> completeTask(
            @PathVariable UUID taskId,
            @PathVariable UUID completedBy) {
        return taskService.completeTask(taskId, completedBy)
                .map(task -> ResponseUtil.success("Task completed successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @PutMapping("/{taskId}/close")
    public ResponseEntity<CommonResponseDto<TaskDto>> closeTask(@PathVariable UUID taskId) {
        return taskService.closeTask(taskId)
                .map(task -> ResponseUtil.success("Task closed successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(taskId, taskDto)
                .map(task -> ResponseUtil.success("Task updated successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteTask(@PathVariable UUID taskId) {
        if (taskService.deleteTask(taskId)) {
            return ResponseUtil.success("Task deleted successfully", null);
        }
        return ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                "Task with ID " + taskId + " not found", null);
    }
}

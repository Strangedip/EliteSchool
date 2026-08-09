package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getAllTasks(HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getAllTask());
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<TaskDto>> createTask(@Valid @RequestBody TaskDto taskDto,
                                                                 HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        UUID creatorId = GatewayAuth.requireUserId(request);
        taskDto.setCreatedBy(creatorId);
        return ResponseUtil.success("Task created successfully", taskService.createTask(taskDto));
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> createTaskFromTemplate(
            @PathVariable UUID templateId,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        UUID creatorId = GatewayAuth.requireUserId(request);
        return taskService.createTaskFromTemplate(templateId, creatorId)
                .map(task -> ResponseUtil.success("Task created from template successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND",
                        "Task template with ID " + templateId + " not found", null));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByStatus(@PathVariable TaskStatus status,
                                                                             HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getTasksByStatus(status));
    }

    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByCreator(@PathVariable UUID createdBy,
                                                                              HttpServletRequest request) {
        GatewayAuth.requireSelfOrRoles(request, createdBy, "ADMIN", "MANAGEMENT", "FACULTY");
        return ResponseUtil.success("Tasks retrieved successfully", taskService.getTasksByCreator(createdBy));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> getTaskById(@PathVariable UUID taskId,
                                                                  HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return taskService.getTaskById(taskId)
                .map(task -> ResponseUtil.success("Task retrieved successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @PutMapping("/{taskId}/close")
    public ResponseEntity<CommonResponseDto<TaskDto>> closeTask(@PathVariable UUID taskId,
                                                                HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        return taskService.closeTask(taskId)
                .map(task -> ResponseUtil.success("Task closed successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskDto taskDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        return taskService.updateTask(taskId, taskDto)
                .map(task -> ResponseUtil.success("Task updated successfully", task))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found", null));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteTask(@PathVariable UUID taskId,
                                                              HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "ADMIN", "MANAGEMENT");
        if (taskService.deleteTask(taskId)) {
            return ResponseUtil.success("Task deleted successfully", null);
        }
        return ResponseUtil.error(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND",
                "Task with ID " + taskId + " not found", null);
    }
}

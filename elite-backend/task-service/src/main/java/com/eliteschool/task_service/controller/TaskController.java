package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create a new task.
     * @param taskDto The task details.
     * @return ResponseEntity with created task
     */
    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<TaskDto>> createTask(@Valid @RequestBody TaskDto taskDto) {
        log.info("Request received to create new task: {}", taskDto.getTitle());
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseUtil.success("Task created successfully", createdTask);
    }

    /**
     * Get all tasks by status.
     * @param status The task status (OPEN, COMPLETED, CLOSED).
     * @return List of tasks.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByStatus(@PathVariable TaskStatus status) {
        log.info("Request received to get tasks with status: {}", status);
        List<TaskDto> tasks = taskService.getTasksByStatus(status);
        return ResponseUtil.success("Tasks retrieved successfully", tasks);
    }

    /**
     * Get all tasks created by a specific faculty/management user.
     * @param createdBy The creator's ID.
     * @return List of tasks.
     */
    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<CommonResponseDto<List<TaskDto>>> getTasksByCreator(@PathVariable UUID createdBy) {
        log.info("Request received to get tasks created by user with ID: {}", createdBy);
        List<TaskDto> tasks = taskService.getTasksByCreator(createdBy);
        return ResponseUtil.success("Tasks retrieved successfully", tasks);
    }

    /**
     * Get task by ID.
     * @param taskId The task ID.
     * @return ResponseEntity with task if found
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<CommonResponseDto<TaskDto>> getTaskById(@PathVariable UUID taskId) {
        log.info("Request received to get task with ID: {}", taskId);
        return taskService.getTaskById(taskId)
                .map(task -> ResponseUtil.success("Task retrieved successfully", task))
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.NOT_FOUND,
                        "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found",
                        "Task not found"
                ));
    }

    /**
     * Complete a task (for SINGLE tasks).
     * @param taskId The task ID.
     * @param completedBy The student ID who completed the task.
     * @return ResponseEntity with updated task
     */
    @PutMapping("/{taskId}/complete/{completedBy}")
    public ResponseEntity<CommonResponseDto<TaskDto>> completeTask(
            @PathVariable UUID taskId, 
            @PathVariable UUID completedBy) {
        
        log.info("Request received to complete task with ID: {} by student: {}", taskId, completedBy);
        return taskService.completeTask(taskId, completedBy)
                .map(task -> ResponseUtil.success("Task completed successfully", task))
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.NOT_FOUND,
                        "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found",
                        "Task not found"
                ));
    }

    /**
     * Close a task.
     * @param taskId The task ID.
     * @return ResponseEntity with updated task
     */
    @PutMapping("/{taskId}/close")
    public ResponseEntity<CommonResponseDto<TaskDto>> closeTask(@PathVariable UUID taskId) {
        log.info("Request received to close task with ID: {}", taskId);
        return taskService.closeTask(taskId)
                .map(task -> ResponseUtil.success("Task closed successfully", task))
                .orElseGet(() -> ResponseUtil.error(
                        HttpStatus.NOT_FOUND,
                        "TASK_NOT_FOUND",
                        "Task with ID " + taskId + " not found",
                        "Task not found"
                ));
    }
}

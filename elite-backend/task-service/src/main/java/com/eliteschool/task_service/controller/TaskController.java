package com.eliteschool.task_service.controller;

import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create a new task.
     * @param task The task details.
     * @return ResponseEntity<Task>
     */
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    /**
     * Get all tasks by status.
     * @param status The task status (OPEN, COMPLETED, CLOSED).
     * @return List of tasks.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    /**
     * Get all tasks created by a specific faculty/management user.
     * @param createdBy The creator's ID.
     * @return List of tasks.
     */
    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<List<Task>> getTasksByCreator(@PathVariable UUID createdBy) {
        return ResponseEntity.ok(taskService.getTasksByCreator(createdBy));
    }

    /**
     * Get task by ID.
     * @param taskId The task ID.
     * @return ResponseEntity<Task>
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID taskId) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Complete a task (for SINGLE tasks).
     * @param taskId The task ID.
     * @param completedBy The student ID who completed the task.
     * @return ResponseEntity<Task>
     */
    @PutMapping("/{taskId}/complete/{completedBy}")
    public ResponseEntity<Task> completeTask(@PathVariable UUID taskId, @PathVariable UUID completedBy) {
        return taskService.completeTask(taskId, completedBy)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Close a task.
     * @param taskId The task ID.
     * @return ResponseEntity<Task>
     */
    @PutMapping("/{taskId}/close")
    public ResponseEntity<Task> closeTask(@PathVariable UUID taskId) {
        return taskService.closeTask(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

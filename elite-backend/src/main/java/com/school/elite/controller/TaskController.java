package com.school.elite.controller;

import com.school.elite.DTO.CommonResponseDto;
import com.school.elite.DTO.TaskRequestDto;
import com.school.elite.entity.Task;
import com.school.elite.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<CommonResponseDto> createTask(@RequestBody TaskRequestDto taskRequestDto) {
        Task task = taskService.createTask(taskRequestDto);
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task created successfully", null, task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task retrieved successfully", null, task));
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task list retrieved successfully", null, tasks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDto> updateTask(@PathVariable Long id, @RequestBody TaskRequestDto updatedTask) {
        Task task = taskService.updateTask(id, updatedTask);
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task updated successfully", null, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task deleted successfully", null, null));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<CommonResponseDto> completeTask(@PathVariable Long id, @RequestParam String completedBy) {
        Task task = taskService.completeTask(id, completedBy);
        return ResponseEntity.ok(CommonResponseDto.createCommonResponseDto(200, "Task completed successfully", null, task));
    }
}

package com.school.elite.service;

import com.school.elite.DTO.TaskCreateRequestDto;
import com.school.elite.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(TaskCreateRequestDto taskCreateRequestDto);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    Task updateTask(Long id, Task task);
    void deleteTask(Long id);
    Task completeTask(Long id, String completedBy);
}
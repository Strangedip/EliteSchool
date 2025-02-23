package com.school.elite.service;

import com.school.elite.DTO.TaskRequestDto;
import com.school.elite.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(TaskRequestDto taskRequestDto);
    Task getTaskById(Long id);
    List<Task> getAllTasks();
    Task updateTask(Long id, TaskRequestDto task);
    void deleteTask(Long id);
    Task completeTask(Long id, String completedBy);
}
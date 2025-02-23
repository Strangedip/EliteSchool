package com.school.elite.service.impl;

import com.school.elite.DTO.TaskCreateRequestDto;
import com.school.elite.entity.Task;
import com.school.elite.exception.ResourceNotFoundException;
import com.school.elite.repository.TaskRepository;
import com.school.elite.service.TaskService;
import com.school.elite.utils.Constants;
import com.school.elite.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    @Override
    public Task createTask(TaskCreateRequestDto taskCreateRequestDto) {
        Task task = Task.builder()
                .createdBy(taskCreateRequestDto.getCreatedBy())
                .createdOn(TimeUtil.getTime())
                .name(taskCreateRequestDto.getName())
                .detail(taskCreateRequestDto.getDetail())
                .reward(taskCreateRequestDto.getReward())
                .status(Constants.TaskStatus.OPEN)
                .completedBy(null)
                .build();
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);
        task.setName(updatedTask.getName());
        task.setDetail(updatedTask.getDetail());
        task.setReward(updatedTask.getReward());
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    public Task completeTask(Long id, String completedBy) {
        Task task = getTaskById(id);
        if (Constants.TaskStatus.CLOSED.equals(task.getStatus())) {
            logger.warn("Task is closed.");
            return task;
        } else if (Constants.TaskStatus.COMPLETED.equals(task.getStatus())) {
            logger.warn("Task is already completed.");
            return task;
        }
        task.setCompletedBy(completedBy);
        task.setStatus(Constants.TaskStatus.COMPLETED);
        return taskRepository.save(task);
    }
}

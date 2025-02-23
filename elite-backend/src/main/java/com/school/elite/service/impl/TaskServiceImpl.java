package com.school.elite.service.impl;

import com.school.elite.DTO.TaskRequestDto;
import com.school.elite.entity.Task;
import com.school.elite.exception.TaskException;
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

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(TaskRequestDto taskRequestDto) {
        Task task = Task.builder()
                .createdBy(taskRequestDto.getCreatedBy())
                .createdOn(TimeUtil.getTime())
                .name(taskRequestDto.getName())
                .detail(taskRequestDto.getDetail())
                .reward(taskRequestDto.getReward())
                .status(Constants.TaskStatus.OPEN)
                .completedBy(null)
                .build();
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return savedTask;
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Task not found with ID: {}", id);
                    return new TaskException.ResourceNotFoundException("Task not found with id: " + id);
                });
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task updateTask(Long id, TaskRequestDto updatedTask) {
        Task task = getTaskById(id);

        if (updatedTask.getName() != null) {
            task.setName(updatedTask.getName());
        }
        if (updatedTask.getDetail() != null) {
            task.setDetail(updatedTask.getDetail());
        }
        if (updatedTask.getReward() != null) {
            task.setReward(updatedTask.getReward());
        }

        Task savedTask = taskRepository.save(task);
        logger.info("Task with ID: {} updated successfully.", id);
        return savedTask;
    }

    @Override
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
        logger.info("Task with ID: {} deleted successfully.", id);
    }

    @Override
    public Task completeTask(Long id, String completedBy) {
        Task task = getTaskById(id);

        if (Constants.TaskStatus.CLOSED.equals(task.getStatus())) {
            logger.warn("Task with ID: {} is closed.", id);
            return task;
        } else if (Constants.TaskStatus.COMPLETED.equals(task.getStatus())) {
            logger.warn("Task with ID: {} is already completed.", id);
            return task;
        }

        task.setCompletedBy(completedBy);
        task.setStatus(Constants.TaskStatus.COMPLETED);
        Task savedTask = taskRepository.save(task);
        logger.info("Task with ID: {} marked as completed by: {}", id, completedBy);
        return savedTask;
    }
}

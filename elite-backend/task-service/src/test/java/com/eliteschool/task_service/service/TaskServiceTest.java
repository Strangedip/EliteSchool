package com.eliteschool.task_service.service;

import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskDto testTaskDto;
    private UUID taskId;
    private UUID creatorId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        creatorId = UUID.randomUUID();

        testTask = new Task();
        testTask.setId(taskId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setTaskType(TaskType.SINGLE);
        testTask.setStatus(TaskStatus.OPEN);
        testTask.setRewardPoints(10);
        testTask.setMinLevel(1);
        testTask.setCreatedBy(creatorId);

        testTaskDto = new TaskDto();
        testTaskDto.setId(taskId);
        testTaskDto.setTitle("Test Task");
        testTaskDto.setDescription("Test Description");
        testTaskDto.setTaskType(TaskType.SINGLE);
        testTaskDto.setStatus(TaskStatus.OPEN);
        testTaskDto.setRewardPoints(10);
        testTaskDto.setMinLevel(1);
        testTaskDto.setCreatedBy(creatorId);
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskDto createdTask = taskService.createTask(testTaskDto);

        // Assert
        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo("Test Task");
        assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.OPEN);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get all tasks")
    void shouldGetAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<TaskDto> allTasks = taskService.getAllTask();

        // Assert
        assertThat(allTasks).hasSize(1);
        assertThat(allTasks.get(0).getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get task by ID")
    void shouldGetTaskById() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act
        Optional<TaskDto> foundTask = taskService.getTaskById(taskId);

        // Assert
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getId()).isEqualTo(taskId);
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    @DisplayName("Should return empty when task not found")
    void shouldReturnEmptyWhenTaskNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<TaskDto> foundTask = taskService.getTaskById(nonExistentId);

        // Assert
        assertThat(foundTask).isEmpty();
        verify(taskRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get tasks by status")
    void shouldGetTasksByStatus() {
        // Arrange
        List<Task> openTasks = Arrays.asList(testTask);
        when(taskRepository.findByStatus(TaskStatus.OPEN)).thenReturn(openTasks);

        // Act
        List<TaskDto> tasks = taskService.getTasksByStatus(TaskStatus.OPEN);

        // Assert
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.OPEN);
        verify(taskRepository, times(1)).findByStatus(TaskStatus.OPEN);
    }

    @Test
    @DisplayName("Should get tasks by creator")
    void shouldGetTasksByCreator() {
        // Arrange
        List<Task> creatorTasks = Arrays.asList(testTask);
        when(taskRepository.findByCreatedBy(creatorId)).thenReturn(creatorTasks);

        // Act
        List<TaskDto> tasks = taskService.getTasksByCreator(creatorId);

        // Assert
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getCreatedBy()).isEqualTo(creatorId);
        verify(taskRepository, times(1)).findByCreatedBy(creatorId);
    }

    @Test
    @DisplayName("Should close task successfully")
    void shouldCloseTaskSuccessfully() {
        // Arrange
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        Optional<TaskDto> closedTask = taskService.closeTask(taskId);

        // Assert
        assertThat(closedTask).isPresent();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Arrange
        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");
        updateDto.setRewardPoints(20);
        updateDto.setMinLevel(1);
        updateDto.setTaskType(TaskType.SINGLE);
        updateDto.setCreatedBy(creatorId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        Optional<TaskDto> updatedTask = taskService.updateTask(taskId, updateDto);

        // Assert
        assertThat(updatedTask).isPresent();
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        // Arrange
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);

        // Act
        boolean deleted = taskService.deleteTask(taskId);

        // Assert
        assertThat(deleted).isTrue();
        verify(taskRepository, times(1)).existsById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent task")
    void shouldReturnFalseWhenDeletingNonExistentTask() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskRepository.existsById(nonExistentId)).thenReturn(false);

        // Act
        boolean deleted = taskService.deleteTask(nonExistentId);

        // Assert
        assertThat(deleted).isFalse();
        verify(taskRepository, times(1)).existsById(nonExistentId);
        verify(taskRepository, never()).deleteById(any(UUID.class));
    }
}


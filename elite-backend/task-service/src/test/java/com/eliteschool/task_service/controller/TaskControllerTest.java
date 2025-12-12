package com.eliteschool.task_service.controller;

import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TaskController Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskDto testTask;
    private UUID taskId;
    private UUID creatorId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        creatorId = UUID.randomUUID();

        testTask = new TaskDto();
        testTask.setId(taskId);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setTaskType(TaskType.SINGLE);
        testTask.setStatus(TaskStatus.OPEN);
        testTask.setRewardPoints(10);
        testTask.setMinLevel(1);
        testTask.setCreatedBy(creatorId);
    }

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasksSuccessfully() throws Exception {
        // Arrange
        List<TaskDto> tasks = Arrays.asList(testTask);
        when(taskService.getAllTask()).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Test Task"));

        verify(taskService, times(1)).getAllTask();
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() throws Exception {
        // Arrange
        when(taskService.createTask(any(TaskDto.class))).thenReturn(testTask);

        // Act & Assert
        mockMvc.perform(post("/api/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.data.title").value("Test Task"))
                .andExpect(jsonPath("$.data.rewardPoints").value(10));

        verify(taskService, times(1)).createTask(any(TaskDto.class));
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void shouldGetTaskByIdSuccessfully() throws Exception {
        // Arrange
        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(testTask));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(taskId.toString()))
                .andExpect(jsonPath("$.data.title").value("Test Task"));

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    @DisplayName("Should return not found for non-existent task")
    void shouldReturnNotFoundForNonExistentTask() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskService.getTaskById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{taskId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).getTaskById(nonExistentId);
    }

    @Test
    @DisplayName("Should get tasks by status successfully")
    void shouldGetTasksByStatusSuccessfully() throws Exception {
        // Arrange
        List<TaskDto> openTasks = Arrays.asList(testTask);
        when(taskService.getTasksByStatus(TaskStatus.OPEN)).thenReturn(openTasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/status/{status}", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("OPEN"));

        verify(taskService, times(1)).getTasksByStatus(TaskStatus.OPEN);
    }

    @Test
    @DisplayName("Should get tasks by creator successfully")
    void shouldGetTasksByCreatorSuccessfully() throws Exception {
        // Arrange
        List<TaskDto> creatorTasks = Arrays.asList(testTask);
        when(taskService.getTasksByCreator(creatorId)).thenReturn(creatorTasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/created-by/{createdBy}", creatorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].createdBy").value(creatorId.toString()));

        verify(taskService, times(1)).getTasksByCreator(creatorId);
    }

    @Test
    @DisplayName("Should complete task successfully")
    void shouldCompleteTaskSuccessfully() throws Exception {
        // Arrange
        UUID completedBy = UUID.randomUUID();
        testTask.setStatus(TaskStatus.COMPLETED);
        when(taskService.completeTask(taskId, completedBy)).thenReturn(Optional.of(testTask));

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{taskId}/complete/{completedBy}", taskId, completedBy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task completed successfully"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        verify(taskService, times(1)).completeTask(taskId, completedBy);
    }

    @Test
    @DisplayName("Should close task successfully")
    void shouldCloseTaskSuccessfully() throws Exception {
        // Arrange
        testTask.setStatus(TaskStatus.CLOSED);
        when(taskService.closeTask(taskId)).thenReturn(Optional.of(testTask));

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{taskId}/close", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task closed successfully"))
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        verify(taskService, times(1)).closeTask(taskId);
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() throws Exception {
        // Arrange
        TaskDto updatedTask = new TaskDto();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setRewardPoints(20);
        updatedTask.setMinLevel(1);
        updatedTask.setTaskType(TaskType.SINGLE);
        updatedTask.setCreatedBy(creatorId);

        when(taskService.updateTask(eq(taskId), any(TaskDto.class))).thenReturn(Optional.of(updatedTask));

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task updated successfully"));

        verify(taskService, times(1)).updateTask(eq(taskId), any(TaskDto.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() throws Exception {
        // Arrange
        when(taskService.deleteTask(taskId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    @DisplayName("Should fail to create task with invalid data")
    void shouldFailToCreateTaskWithInvalidData() throws Exception {
        // Arrange
        TaskDto invalidTask = new TaskDto();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/tasks/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }
}


package com.eliteschool.task_service.controller;

import com.eliteschool.common_utils.security.GatewayHeaders;
import com.eliteschool.task_service.dto.TaskDto;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.service.TaskService;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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
    private JsonMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskDto testTask;
    private UUID taskId;
    private UUID creatorId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        studentId = UUID.randomUUID();

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

    private RequestPostProcessor asAdmin() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, UUID.randomUUID().toString());
            request.addHeader(GatewayHeaders.ROLE, "ADMIN");
            request.addHeader(GatewayHeaders.USERNAME, "admin");
            return request;
        };
    }

    private RequestPostProcessor asFaculty() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, creatorId.toString());
            request.addHeader(GatewayHeaders.ROLE, "FACULTY");
            request.addHeader(GatewayHeaders.USERNAME, "faculty");
            return request;
        };
    }

    private RequestPostProcessor asStudent() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, studentId.toString());
            request.addHeader(GatewayHeaders.ROLE, "STUDENT");
            request.addHeader(GatewayHeaders.USERNAME, "student");
            return request;
        };
    }

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasksSuccessfully() throws Exception {
        List<TaskDto> tasks = Arrays.asList(testTask);
        when(taskService.getAllTask()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/all").with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Test Task"));

        verify(taskService, times(1)).getAllTask();
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks/create")
                .with(asFaculty())
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
        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(testTask));

        mockMvc.perform(get("/api/tasks/{taskId}", taskId).with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(taskId.toString()))
                .andExpect(jsonPath("$.data.title").value("Test Task"));

        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    @DisplayName("Should return not found for non-existent task")
    void shouldReturnNotFoundForNonExistentTask() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(taskService.getTaskById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/{taskId}", nonExistentId).with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("TASK_NOT_FOUND"));

        verify(taskService, times(1)).getTaskById(nonExistentId);
    }

    @Test
    @DisplayName("Should get tasks by status successfully")
    void shouldGetTasksByStatusSuccessfully() throws Exception {
        List<TaskDto> openTasks = Arrays.asList(testTask);
        when(taskService.getTasksByStatus(TaskStatus.OPEN)).thenReturn(openTasks);

        mockMvc.perform(get("/api/tasks/status/{status}", "OPEN").with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("OPEN"));

        verify(taskService, times(1)).getTasksByStatus(TaskStatus.OPEN);
    }

    @Test
    @DisplayName("Should get tasks by creator successfully")
    void shouldGetTasksByCreatorSuccessfully() throws Exception {
        List<TaskDto> creatorTasks = Arrays.asList(testTask);
        when(taskService.getTasksByCreator(creatorId)).thenReturn(creatorTasks);

        mockMvc.perform(get("/api/tasks/created-by/{createdBy}", creatorId).with(asFaculty()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].createdBy").value(creatorId.toString()));

        verify(taskService, times(1)).getTasksByCreator(creatorId);
    }

    @Test
    @DisplayName("Should close task successfully")
    void shouldCloseTaskSuccessfully() throws Exception {
        testTask.setStatus(TaskStatus.CLOSED);
        when(taskService.closeTask(taskId)).thenReturn(Optional.of(testTask));

        mockMvc.perform(put("/api/tasks/{taskId}/close", taskId).with(asFaculty()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task closed successfully"))
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        verify(taskService, times(1)).closeTask(taskId);
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskDto updatedTask = new TaskDto();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setRewardPoints(20);
        updatedTask.setMinLevel(1);
        updatedTask.setTaskType(TaskType.SINGLE);
        updatedTask.setCreatedBy(creatorId);

        when(taskService.updateTask(eq(taskId), any(TaskDto.class))).thenReturn(Optional.of(updatedTask));

        mockMvc.perform(put("/api/tasks/{taskId}", taskId)
                .with(asFaculty())
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
        when(taskService.deleteTask(taskId)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/{taskId}", taskId).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));

        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    @DisplayName("Should fail to create task with invalid data")
    void shouldFailToCreateTaskWithInvalidData() throws Exception {
        TaskDto invalidTask = new TaskDto();

        mockMvc.perform(post("/api/tasks/create")
                .with(asFaculty())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTask)))
                .andExpect(status().isBadRequest());
    }
}

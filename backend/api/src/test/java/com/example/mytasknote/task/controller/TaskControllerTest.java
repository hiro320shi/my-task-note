package com.example.mytasknote.task.controller;

import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;
import com.example.mytasknote.common.exception.NotFoundException;
import com.example.mytasknote.task.dto.TaskCreateRequest;
import com.example.mytasknote.task.dto.TaskResponse;
import com.example.mytasknote.task.dto.TaskUpdateRequest;
import com.example.mytasknote.task.service.TaskService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TaskService taskService;

    // SecurityConfig が要求するフィルタをモック
    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    // ----------------------------
    // 正常系
    // ----------------------------

    @Test
    void createTask_returns201_andBody() throws Exception {
        TaskResponse response = new TaskResponse(
                1L,
                "テストタスク",
                "説明",
                "2025-12-31",
                false
        );

        Mockito.when(taskService.createTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.any(TaskCreateRequest.class)))
                .thenReturn(response);

        var body = """
                {
                  "title": "テストタスク",
                  "description": "説明",
                  "dueDate": "2025-12-31"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("テストタスク"));

        // Serviceに渡ったDTOの中身を検証（dueDateがLocalDate化されていること）
        ArgumentCaptor<TaskCreateRequest> captor = ArgumentCaptor.forClass(TaskCreateRequest.class);
        Mockito.verify(taskService).createTask(ArgumentMatchers.eq("testuser01"), captor.capture());
        TaskCreateRequest captured = captor.getValue();
        assertEquals(LocalDate.of(2025, 12, 31), captured.getDueDate());
    }

    @Test
    void listTasks_returns200_andList() throws Exception {
        List<TaskResponse> responses = List.of(
                new TaskResponse(1L, "タスク1", "説明1", null, false),
                new TaskResponse(2L, "タスク2", "説明2", "2025-12-31", true)
        );

        Mockito.when(taskService.listTasks("testuser01"))
                .thenReturn(responses);

        mockMvc.perform(get("/api/tasks")
                        .principal(() -> "testuser01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("タスク1"));
    }

    @Test
    void updateTask_returns200_andUpdatedBody() throws Exception {
        TaskResponse response = new TaskResponse(
                1L,
                "更新後タイトル",
                "更新後説明",
                "2025-12-31",
                true
        );

        Mockito.when(taskService.updateTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.any(TaskUpdateRequest.class)))
                .thenReturn(response);

        var body = """
                {
                  "title": "更新後タイトル",
                  "description": "更新後説明",
                  "dueDate": "2025-12-31",
                  "completed": true
                }
                """;

        mockMvc.perform(put("/api/tasks/1")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新後タイトル"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTask_returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                        .principal(() -> "testuser01"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).deleteTask("testuser01", 1L);
    }

    // ----------------------------
    // 異常系
    // ----------------------------

    @Test
    void createTask_returns400_whenTitleBlank() throws Exception {
        var body = """
                {
                  "title": "",
                  "description": "説明",
                  "dueDate": "2025-12-31"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void updateTask_returns404_whenNotFound() throws Exception {
        Mockito.when(taskService.updateTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.eq(999L),
                        ArgumentMatchers.any(TaskUpdateRequest.class)))
                .thenThrow(new NotFoundException("Task not found"));

        var body = """
                {
                  "title": "更新後タイトル"
                }
                """;

        mockMvc.perform(put("/api/tasks/999")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    @Test
    void deleteTask_returns404_whenNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Task not found"))
                .when(taskService).deleteTask("testuser01", 999L);

        mockMvc.perform(delete("/api/tasks/999")
                        .principal(() -> "testuser01"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Task not found"));
    }

    // ----------------------------
    // 境界値
    // ----------------------------

    @Test
    void createTask_returns201_whenTitleIs255() throws Exception {
        String title255 = repeat('a', 255);

        Mockito.when(taskService.createTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.any(TaskCreateRequest.class)))
                .thenReturn(new TaskResponse(1L, title255, "desc", "2025-12-31", false));

        String body = """
                {
                  "title": "%s",
                  "description": "desc",
                  "dueDate": "2025-12-31"
                }
                """.formatted(title255);

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(title255));
    }

    @Test
    void createTask_returns400_whenTitleIs256() throws Exception {
        String title256 = repeat('a', 256);

        String body = """
                {
                  "title": "%s",
                  "description": "desc",
                  "dueDate": "2025-12-31"
                }
                """.formatted(title256);

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void createTask_returns201_whenDescriptionIs1000() throws Exception {
        String desc1000 = repeat('b', 1000);

        Mockito.when(taskService.createTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.any(TaskCreateRequest.class)))
                .thenReturn(new TaskResponse(1L, "ok", desc1000, null, false));

        String body = """
                {
                  "title": "ok",
                  "description": "%s"
                }
                """.formatted(desc1000);

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(desc1000));
    }

    @Test
    void createTask_returns400_whenDescriptionIs1001() throws Exception {
        String desc1001 = repeat('b', 1001);

        String body = """
                {
                  "title": "ok",
                  "description": "%s"
                }
                """.formatted(desc1001);

        mockMvc.perform(post("/api/tasks")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.description").exists());
    }

    @Test
    void updateTask_returns200_whenTitleIs255() throws Exception {
        String title255 = repeat('a', 255);

        Mockito.when(taskService.updateTask(
                        ArgumentMatchers.eq("testuser01"),
                        ArgumentMatchers.eq(1L),
                        ArgumentMatchers.any(TaskUpdateRequest.class)))
                .thenReturn(new TaskResponse(1L, title255, null, null, false));

        String body = """
                {
                  "title": "%s"
                }
                """.formatted(title255);

        mockMvc.perform(put("/api/tasks/1")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title255));
    }

    @Test
    void updateTask_returns400_whenTitleIs256() throws Exception {
        String title256 = repeat('a', 256);

        String body = """
                {
                  "title": "%s"
                }
                """.formatted(title256);

        mockMvc.perform(put("/api/tasks/1")
                        .principal(() -> "testuser01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.title").exists());
    }
}
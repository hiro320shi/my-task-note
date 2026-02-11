package com.example.mytasknote;

import com.example.mytasknote.task.controller.TaskController;
import com.example.mytasknote.task.service.TaskService;
import com.example.mytasknote.auth.jwt.JwtAuthenticationFilter;

import com.example.mytasknote.task.dto.TaskCreateRequest;
import com.example.mytasknote.task.dto.TaskUpdateRequest;
import com.example.mytasknote.task.dto.TaskResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.ArgumentCaptor;
import java.time.LocalDate;
import java.util.List;

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

        // ★ここから追加：Serviceに渡ったDTOの中身を検証
        ArgumentCaptor<TaskCreateRequest> captor = ArgumentCaptor.forClass(TaskCreateRequest.class);
        Mockito.verify(taskService).createTask(ArgumentMatchers.eq("testuser01"), captor.capture());

        TaskCreateRequest captured = captor.getValue();
        // dueDate が LocalDate になっていること（ここが今回のリファクタの肝）
        org.junit.jupiter.api.Assertions.assertEquals(LocalDate.of(2025, 12, 31), captured.getDueDate());
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
}

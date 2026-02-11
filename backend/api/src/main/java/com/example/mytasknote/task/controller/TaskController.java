package com.example.mytasknote.task.controller;

import com.example.mytasknote.task.service.TaskService;
import com.example.mytasknote.task.dto.TaskCreateRequest;
import com.example.mytasknote.task.dto.TaskResponse;
import com.example.mytasknote.task.dto.TaskUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 5日目①: タスク登録 POST /api/tasks
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            Principal principal,
            @Valid @RequestBody TaskCreateRequest request) {

        String username = principal.getName();

        TaskResponse created = taskService.createTask(username, request);

        // Location ヘッダも付けておく
        return ResponseEntity
                .created(URI.create("/api/tasks/" + created.getId()))
                .body(created);
    }

    // 5日目②: タスク一覧 GET /api/tasks
    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(Principal principal) {
        String username = principal.getName();
        List<TaskResponse> tasks = taskService.listTasks(username);
        return ResponseEntity.ok(tasks);
    }

    // 6日目①: タスク更新 PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {

        String username = principal.getName();
        TaskResponse updated = taskService.updateTask(username, id, request);
        return ResponseEntity.ok(updated);
    }

    // 6日目②: タスク削除 DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            Principal principal,
            @PathVariable Long id) {

        String username = principal.getName();
        taskService.deleteTask(username, id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.mytasknote.task.dto;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String dueDate;
    private boolean completed;

    public TaskResponse(Long id, String title, String description, String dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // getter だけでOK（Immutable風）
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
}

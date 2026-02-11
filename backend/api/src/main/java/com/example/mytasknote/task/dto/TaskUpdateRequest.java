package com.example.mytasknote.task.dto;

import jakarta.validation.constraints.Size;

public class TaskUpdateRequest {

    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    private String dueDate;

    private Boolean completed;

    // ----- getter / setter -----

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}

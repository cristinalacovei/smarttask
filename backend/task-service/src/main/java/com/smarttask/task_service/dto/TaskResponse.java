package com.smarttask.task_service.dto;


import com.smarttask.task_service.model.Priority;
import com.smarttask.task_service.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private TaskStatus status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;

    public TaskResponse() {
    }

    public TaskResponse(
            Long id,
            String title,
            String description,
            Priority priority,
            TaskStatus status,
            LocalDate deadline,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long userId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUserId() {
        return userId;
    }
}
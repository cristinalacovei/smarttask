package com.smarttask.ai_service.dto;

import java.time.LocalDate;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate deadline;
    private Long userId;

    public TaskResponse() {
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

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Long getUserId() {
        return userId;
    }
}
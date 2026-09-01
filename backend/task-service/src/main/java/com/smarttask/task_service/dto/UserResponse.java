package com.smarttask.task_service.dto;

public class UserResponse {

    private Long id;
    private String username;
    private String email;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
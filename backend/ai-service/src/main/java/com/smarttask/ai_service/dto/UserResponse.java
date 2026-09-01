package com.smarttask.ai_service.dto;

public class UserResponse {

    private Long id;
    private String username;
    private String email;

    public UserResponse() {
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
package com.smarttask.task_service.controller;

import com.smarttask.task_service.dto.CreateTaskRequest;
import com.smarttask.task_service.dto.TaskResponse;
import com.smarttask.task_service.dto.UpdateTaskRequest;
import com.smarttask.task_service.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping
    public List<TaskResponse> getAllTasks(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.getTasksForCurrentUser(
                jwt.getSubject()
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.getTaskById(
                id,
                jwt.getSubject()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public List<TaskResponse> getTasksByUser(
            @PathVariable Long userId
    ) {
        return taskService.getTasksByUser(userId);
    }

    @PreAuthorize("hasRole('AI_SERVICE')")
    @GetMapping("/internal/user/{userId}")
    public List<TaskResponse> getTasksForAi(
            @PathVariable Long userId
    ) {
        return taskService.getTasksByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public TaskResponse createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.createTask(
                request,
                jwt.getSubject()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return taskService.updateTask(
                id,
                request,
                jwt.getSubject()
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        taskService.deleteTask(
                id,
                jwt.getSubject()
        );
    }
}
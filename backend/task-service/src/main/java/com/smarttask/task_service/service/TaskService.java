package com.smarttask.task_service.service;

import com.smarttask.task_service.client.UserServiceClient;
import com.smarttask.task_service.dto.CreateTaskRequest;
import com.smarttask.task_service.dto.TaskResponse;
import com.smarttask.task_service.dto.UpdateTaskRequest;
import com.smarttask.task_service.dto.UserResponse;
import com.smarttask.task_service.model.Task;
import com.smarttask.task_service.model.TaskStatus;
import com.smarttask.task_service.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;

    public TaskService(
            TaskRepository taskRepository,
            UserServiceClient userServiceClient
    ) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient;
    }

    public List<TaskResponse> getTasksForCurrentUser(
            String keycloakUserId
    ) {

        UserResponse user =
                userServiceClient.getUserByKeycloakUserId(keycloakUserId);

        return getTasksByUser(user.getId());
    }

    public List<TaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse getTaskById(
            Long id,
            String keycloakUserId
    ) {

        UserResponse user =
                userServiceClient.getUserByKeycloakUserId(keycloakUserId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        verifyOwnership(task, user.getId());

        return toResponse(task);
    }

    public TaskResponse createTask(
            CreateTaskRequest request,
            String keycloakUserId
    ) {

        UserResponse user =
                userServiceClient.getUserByKeycloakUserId(keycloakUserId);

        LocalDateTime now = LocalDateTime.now();

        Task task = new Task(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                TaskStatus.TODO,
                request.getDeadline(),
                now,
                now,
                user.getId()
        );

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(
            Long id,
            UpdateTaskRequest request,
            String keycloakUserId
    ) {

        UserResponse user =
                userServiceClient.getUserByKeycloakUserId(keycloakUserId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        verifyOwnership(task, user.getId());

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());
        task.setUpdatedAt(LocalDateTime.now());

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(
            Long id,
            String keycloakUserId
    ) {

        UserResponse user =
                userServiceClient.getUserByKeycloakUserId(keycloakUserId);

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Task not found")
                );

        verifyOwnership(task, user.getId());

        taskRepository.delete(task);
    }

    private void verifyOwnership(Task task, Long userId) {

        if (!task.getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUserId()
        );
    }
}
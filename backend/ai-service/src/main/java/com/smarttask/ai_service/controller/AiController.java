package com.smarttask.ai_service.controller;

import com.smarttask.ai_service.client.TaskServiceClient;
import com.smarttask.ai_service.client.UserServiceClient;
import com.smarttask.ai_service.dto.TaskResponse;
import com.smarttask.ai_service.dto.UserResponse;
import com.smarttask.ai_service.service.AiService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final TaskServiceClient taskServiceClient;
    private final UserServiceClient userServiceClient;

    public AiController(
            AiService aiService,
            TaskServiceClient taskServiceClient,
            UserServiceClient userServiceClient
    ) {
        this.aiService = aiService;
        this.taskServiceClient = taskServiceClient;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/study-plan")
    public String generateStudyPlan(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String keycloakUserId = jwt.getSubject();

        UserResponse user =
                userServiceClient.getUserByKeycloakId(keycloakUserId);

        List<TaskResponse> tasks =
                taskServiceClient.getTasksForUser(user.getId());

        if (tasks.isEmpty()) {
            return "You currently have no tasks to plan.";
        }


        String prompt = buildPrompt(user, tasks);

        return aiService.generatePlan(prompt);
    }

    private String buildPrompt(
            UserResponse user,
            List<TaskResponse> tasks
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("Today: ")
                .append(LocalDate.now());

        prompt.append("\nUsername: ")
                .append(user.getUsername());

        prompt.append("\n\n<tasks>");

        int taskNumber = 1;

        for (TaskResponse task : tasks) {

            prompt.append("\n\nTask #")
                    .append(taskNumber++);

            prompt.append("\nTitle: ")
                    .append(task.getTitle());

            prompt.append("\nDescription: ")
                    .append(task.getDescription());

            prompt.append("\nPriority: ")
                    .append(task.getPriority());

            prompt.append("\nStatus: ")
                    .append(task.getStatus());

            prompt.append("\nDeadline: ")
                    .append(task.getDeadline());
        }

        prompt.append("\n\n</tasks>");

        return prompt.toString();
    }
}
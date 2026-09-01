package com.smarttask.ai_service.client;

import com.smarttask.ai_service.dto.TaskResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Component
public class TaskServiceClient {

    private final RestClient restClient;
    private final String taskServiceUrl;

    public TaskServiceClient(
            RestClient restClient,
            @Value("${services.task-service.url}") String taskServiceUrl
    ) {
        this.restClient = restClient;
        this.taskServiceUrl = taskServiceUrl;
    }

    public List<TaskResponse> getTasksForUser(Long userId) {

        TaskResponse[] response = restClient
                .get()
                .uri(
                        taskServiceUrl + "/api/tasks/internal/user/{userId}",
                        userId
                )
                .attributes(clientRegistrationId("ai-service"))
                .retrieve()
                .body(TaskResponse[].class);

        if (response == null) {
            return List.of();
        }

        return Arrays.asList(response);
    }
}
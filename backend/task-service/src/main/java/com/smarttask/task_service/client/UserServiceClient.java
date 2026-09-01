package com.smarttask.task_service.client;

import com.smarttask.task_service.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver.principal;

@Service
public class UserServiceClient {

    private final RestClient restClient;
    private final String userServiceUrl;

    public UserServiceClient(
            RestClient restClient,
            @Value("${services.user-service.url}") String userServiceUrl
    ) {
        this.restClient = restClient;
        this.userServiceUrl = userServiceUrl;
    }
    public UserResponse getUserByKeycloakUserId(String keycloakUserId) {

        return restClient.get()
                .uri(
                        userServiceUrl + "/api/users/internal/keycloak/{keycloakUserId}",
                        keycloakUserId
                )
                .attributes(clientRegistrationId("task-service"))
                .attributes(principal("task-service"))
                .retrieve()
                .body(UserResponse.class);
    }
}
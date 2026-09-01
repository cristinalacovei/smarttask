package com.smarttask.ai_service.client;

import com.smarttask.ai_service.dto.UserResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Component
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


    public UserResponse getUserByKeycloakId(String keycloakUserId) {

        return restClient
                .get()
                .uri(
                        userServiceUrl + "/api/users/internal/keycloak/{keycloakUserId}",
                        keycloakUserId
                )
                .attributes(clientRegistrationId("ai-service"))
                .retrieve()
                .body(UserResponse.class);
    }
}
package com.smarttask.user_service.controller;

import com.smarttask.user_service.dto.CreateUserRequest;
import com.smarttask.user_service.dto.UserResponse;
import com.smarttask.user_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('AI_SERVICE', 'TASK_SERVICE')")
    @GetMapping("/internal/keycloak/{keycloakUserId}")
    public UserResponse getUserByKeycloakId(
            @PathVariable String keycloakUserId
    ) {
        return userService.getUserByKeycloakUserId(keycloakUserId);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PostMapping("/me")
    public UserResponse getOrCreateCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return userService.getOrCreateCurrentUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @RequestBody CreateUserRequest request
    ) {
        return userService.createUser(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
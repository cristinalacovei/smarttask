package com.smarttask.user_service.service;

import com.smarttask.user_service.dto.CreateUserRequest;
import com.smarttask.user_service.dto.UserResponse;
import com.smarttask.user_service.model.User;
import com.smarttask.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toResponse(user);
    }

    public UserResponse createUser(CreateUserRequest request) {

        User user = new User(
                null,
                request.getUsername(),
                request.getEmail(),
                request.getKeycloakUserId()
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public UserResponse getUserByKeycloakUserId(String keycloakUserId) {

        User user = userRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toResponse(user);
    }

    public UserResponse getOrCreateCurrentUser(
            String keycloakUserId,
            String username,
            String email
    ) {

        return userRepository.findByKeycloakUserId(keycloakUserId)
                .map(this::toResponse)
                .orElseGet(() -> {

                    User user = new User(
                            null,
                            username,
                            email,
                            keycloakUserId
                    );

                    User savedUser = userRepository.save(user);

                    return toResponse(savedUser);
                });
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
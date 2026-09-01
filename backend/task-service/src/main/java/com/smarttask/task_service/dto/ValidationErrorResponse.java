package com.smarttask.task_service.dto;

import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String message,
        Map<String, String> errors
) {
}
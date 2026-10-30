package com.investra.chatbot_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    String message
) {}
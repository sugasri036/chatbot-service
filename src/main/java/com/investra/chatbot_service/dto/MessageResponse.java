
package com.investra.chatbot_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    String role,
    String content,
    LocalDateTime createdAt
) {}

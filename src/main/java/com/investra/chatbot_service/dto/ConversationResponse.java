
package com.investra.chatbot_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(
    UUID id,
    String title,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}


package com.investra.chatbot_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chatbot_messages",
       indexes = @Index(name = "idx_chat_message_conversation",
                        columnList = "conversation_id, created_at"))
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ChatMessage() {}

    public ChatMessage(UUID conversationId, String role, String content) {
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

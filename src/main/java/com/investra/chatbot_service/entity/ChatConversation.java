package com.investra.chatbot_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chatbot_conversations")
public class ChatConversation {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(length = 200)
    private String title;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ChatConversation() {}

    public ChatConversation(String userId, String title) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
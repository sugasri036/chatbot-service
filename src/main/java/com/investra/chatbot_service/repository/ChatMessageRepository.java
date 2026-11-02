package com.investra.chatbot_service.repository;

import com.investra.chatbot_service.entity.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findTop10ByConversationIdOrderByCreatedAtDesc(
            UUID conversationId
    );

    List<ChatMessage> findAllByConversationIdOrderByCreatedAtAsc(
            UUID conversationId
    );

    void deleteAllByConversationId(UUID conversationId);
}
package com.investra.chatbot_service.repository;

import com.investra.chatbot_service.entity.ChatConversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, UUID> {

    Optional<ChatConversation> findByIdAndUserId(
            UUID id,
            String userId
    );

    List<ChatConversation> findAllByUserIdOrderByUpdatedAtDesc(
            String userId
    );
}
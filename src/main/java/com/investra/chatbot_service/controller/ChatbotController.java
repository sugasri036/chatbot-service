
package com.investra.chatbot_service.controller;

import com.investra.chatbot_service.dto.ChatRequest;
import com.investra.chatbot_service.dto.ChatResponse;
import com.investra.chatbot_service.dto.ConversationResponse;
import com.investra.chatbot_service.dto.MessageResponse;

import com.investra.chatbot_service.entity.ChatConversation;
import com.investra.chatbot_service.entity.ChatMessage;

import com.investra.chatbot_service.repository.ChatConversationRepository;
import com.investra.chatbot_service.repository.ChatMessageRepository;
import com.investra.chatbot_service.service.ChatbotService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;

    public ChatbotController(
            ChatbotService chatbotService,
            ChatConversationRepository conversationRepository,
            ChatMessageRepository messageRepository) {

        this.chatbotService = chatbotService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    // Send a message or continue a conversation
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        ChatResponse response = chatbotService.chat(
                userId,
                request.message(),
                request.conversationId()
        );

        return ResponseEntity.ok(response);
    }

    // List the logged-in user's conversations
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<ConversationResponse> conversations =
                conversationRepository
                        .findAllByUserIdOrderByUpdatedAtDesc(userId)
                        .stream()
                        .map(this::toConversationResponse)
                        .toList();

        return ResponseEntity.ok(conversations);
    }

    // Load messages from a conversation
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable String conversationId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        UUID id = parseId(conversationId);

        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean owned = conversationRepository
                .findByIdAndUserId(id, userId)
                .isPresent();

        if (!owned) {
            return ResponseEntity.notFound().build();
        }

        List<MessageResponse> messages =
                messageRepository
                        .findAllByConversationIdOrderByCreatedAtAsc(id)
                        .stream()
                        .map(this::toMessageResponse)
                        .toList();

        return ResponseEntity.ok(messages);
    }

    // Delete a conversation and its messages
    @DeleteMapping("/conversations/{conversationId}")
    @Transactional
    public ResponseEntity<Void> deleteConversation(
            @PathVariable String conversationId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        UUID id = parseId(conversationId);

        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatConversation conversation = conversationRepository
                .findByIdAndUserId(id, userId)
                .orElse(null);

        if (conversation == null) {
            return ResponseEntity.notFound().build();
        }

        messageRepository.deleteAllByConversationId(id);
        conversationRepository.delete(conversation);

        return ResponseEntity.noContent().build();
    }

    private String getUserId(Jwt jwt) {

        if (jwt == null) {
            return null;
        }

        return jwt.getClaimAsString("userId");
    }

    private UUID parseId(String value) {

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private ConversationResponse toConversationResponse(
            ChatConversation conversation) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageResponse toMessageResponse(ChatMessage message) {

        return new MessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}

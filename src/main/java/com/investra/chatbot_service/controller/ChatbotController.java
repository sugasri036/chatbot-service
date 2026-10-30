package com.investra.chatbot_service.controller;

import com.investra.chatbot_service.dto.ChatRequest;
import com.investra.chatbot_service.dto.ChatResponse;
import com.investra.chatbot_service.service.ChatbotService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request) {

        ChatResponse response =
                chatbotService.chat(request.message());

        return ResponseEntity.ok(response);
    }
}
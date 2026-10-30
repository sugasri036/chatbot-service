
package com.investra.chatbot_service.service;

import com.investra.chatbot_service.dto.ChatResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private final RestClient restClient;
    private final String model;

    public ChatbotService(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model) {

        this.restClient = RestClient.create(baseUrl);
        this.model = model;
    }

    public ChatResponse chat(String message) {

        String systemPrompt = """
            You are the AI assistant for Investra,
            a mutual fund investment application.

            Responsibilities:
            - Explain mutual funds, NAV, SIP and returns.
            - Explain investment concepts and risks.
            - Help users understand Investra features.
            - Give clear and beginner-friendly answers.

            Safety rules:
            - Never invent account balances or NAV values.
            - Never guarantee investment returns.
            - Do not provide personalized financial advice.
            - You do not have access to user accounts or live data.
            - If information is unavailable, say so honestly.
            """;

        Map<String, Object> request = Map.of(
            "model", model,
            "stream", false,
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content", systemPrompt
                ),
                Map.of(
                    "role", "user",
                    "content", message
                )
            )
        );

        Map<?, ?> response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                "No response received from Ollama"
            );
        }

        Object messageObject = response.get("message");

        if (!(messageObject instanceof Map<?, ?> messageMap)) {
            throw new IllegalStateException(
                "Invalid response received from Ollama"
            );
        }

        Object content = messageMap.get("content");

        if (!(content instanceof String reply) || reply.isBlank()) {
            throw new IllegalStateException(
                "Ollama returned an empty response"
            );
        }

        return new ChatResponse(reply);
    }
}

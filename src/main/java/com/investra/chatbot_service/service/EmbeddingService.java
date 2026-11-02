
package com.investra.chatbot_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient;
    private final String embeddingModel;

    public EmbeddingService(
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.embedding-model:nomic-embed-text}") String embeddingModel,
            @Value("${ollama.proxy-token:}") String proxyToken) {

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl);

        if (proxyToken != null && !proxyToken.isBlank()) {
            builder.defaultHeader(
                    "Authorization",
                    "Bearer " + proxyToken
            );
        }

        this.restClient = builder.build();
        this.embeddingModel = embeddingModel;
    }

    public List<Double> generateEmbedding(String text) {

        Map<String, Object> request = Map.of(
                "model", embeddingModel,
                "input", text
        );

        Map<?, ?> response = restClient.post()
                .uri("/api/embed")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "No embedding response received from Ollama"
            );
        }

        Object embeddingsObject = response.get("embeddings");

        if (!(embeddingsObject instanceof List<?> embeddings)
                || embeddings.isEmpty()) {
            throw new IllegalStateException(
                    "Ollama returned no embeddings"
            );
        }

        Object firstEmbedding = embeddings.get(0);

        if (!(firstEmbedding instanceof List<?> values)) {
            throw new IllegalStateException(
                    "Invalid embedding format received from Ollama"
            );
        }

        return values.stream()
                .map(value -> ((Number) value).doubleValue())
                .toList();
    }
}

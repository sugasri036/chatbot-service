
package com.investra.chatbot_service.service;

import java.util.List;

public record DocumentChunk(
        String source,
        String content,
        List<Double> embedding
) {
}


package com.investra.chatbot_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private static final Logger log =
            LoggerFactory.getLogger(KnowledgeBaseService.class);

    private final EmbeddingService embeddingService;

    private final List<DocumentChunk> chunks = new ArrayList<>();

    private static final int MAX_CHUNK_SIZE = 1200;
    private static final int TOP_K = 3;

    public KnowledgeBaseService(
            EmbeddingService embeddingService
    ) throws IOException {

        this.embeddingService = embeddingService;

        loadKnowledgeBase();
    }

    // STEP 1: Load all Markdown files
    private void loadKnowledgeBase() throws IOException {

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath*:knowledge/*.md");

        for (Resource resource : resources) {

            String content;

            try (var inputStream = resource.getInputStream()) {
                content = new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            List<String> documentChunks =
                    splitIntoChunks(content);

            int processedChunks = 0;

            for (String chunk : documentChunks) {

                if (chunk.isBlank()) {
                    continue;
                }

                // Generate embedding for each chunk
                List<Double> embedding =
                        embeddingService.generateEmbedding(chunk);

                // Store document, content and embedding
                chunks.add(new DocumentChunk(
                        resource.getFilename(),
                        chunk,
                        embedding
                ));

                processedChunks++;
            }

            log.info(
                    "Processed: {} | Chunks: {}",
                    resource.getFilename(),
                    processedChunks
            );
        }

        if (chunks.isEmpty()) {
            throw new IllegalStateException(
                    "No knowledge chunks were generated."
            );
        }

        log.info(
                "Knowledge base ready. Total chunks: {}",
                chunks.size()
        );
    }

    // STEP 2: Split Markdown into heading-aware paragraph chunks
    private List<String> splitIntoChunks(String content) {

        List<String> result = new ArrayList<>();

        StringBuilder currentChunk = new StringBuilder();

        List<String> headings = new ArrayList<>();

        // Split Markdown into paragraphs
        String[] paragraphs = content.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {

            paragraph = paragraph.trim();

            if (paragraph.isEmpty()) {
                continue;
            }

            // Detect Markdown headings
            if (paragraph.matches("^#{1,6}\\s+.*")) {

                addChunk(result, currentChunk);

                int headingLevel = 0;

                while (headingLevel < paragraph.length()
                        && paragraph.charAt(headingLevel) == '#') {

                    headingLevel++;
                }

                String headingText =
                        paragraph.substring(headingLevel).trim();

                // Remove headings at the same or deeper level
                while (headings.size() >= headingLevel) {
                    headings.remove(headings.size() - 1);
                }

                headings.add(headingText);

                continue;
            }

            // Rebuild heading context
            StringBuilder headingContext =
                    new StringBuilder();

            for (int i = 0; i < headings.size(); i++) {

                headingContext.append("#".repeat(i + 1))
                        .append(" ")
                        .append(headings.get(i))
                        .append("\n");
            }

            String context = headingContext.toString();

            // Handle oversized paragraphs
            if (paragraph.length() + context.length()
                    > MAX_CHUNK_SIZE) {

                addChunk(result, currentChunk);

                int availableSize = Math.max(
                        1,
                        MAX_CHUNK_SIZE - context.length()
                );

                for (int i = 0; i < paragraph.length();
                     i += availableSize) {

                    int end = Math.min(
                            i + availableSize,
                            paragraph.length()
                    );

                    result.add(
                            context + paragraph.substring(i, end)
                    );
                }

                continue;
            }

            String completeParagraph =
                    context + paragraph;

            // Start a new chunk if the size limit is reached
            if (currentChunk.length() > 0
                    && currentChunk.length()
                    + completeParagraph.length() + 2
                    > MAX_CHUNK_SIZE) {

                addChunk(result, currentChunk);
            }

            if (currentChunk.length() == 0) {
                currentChunk.append(context);
            }

            if (currentChunk.length() > 0
                    && !currentChunk.toString().endsWith("\n")) {

                currentChunk.append("\n");
            }

            currentChunk.append(paragraph)
                    .append("\n\n");
        }

        // Save final chunk
        addChunk(result, currentChunk);

        return result;
    }

    // STEP 3: Add a chunk safely
    private void addChunk(
            List<String> result,
            StringBuilder currentChunk
    ) {

        if (!currentChunk.isEmpty()) {

            result.add(currentChunk.toString().trim());

            currentChunk.setLength(0);
        }
    }

    // STEP 4: Semantic retrieval using cosine similarity
    public String retrieveRelevantKnowledge(String question) {

        // Generate embedding for user question
        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        // Calculate similarity and rank chunks
        List<ScoredChunk> results = chunks.stream()

                .map(chunk -> new ScoredChunk(
                        chunk,
                        cosineSimilarity(
                                questionEmbedding,
                                chunk.embedding()
                        )
                ))

                .sorted(Comparator.comparingDouble(
                        ScoredChunk::score
                ).reversed())

                .limit(TOP_K)

                .toList();

        // STEP 5: Log retrieval information
        log.debug("========================================");
        log.debug("RAG USER QUESTION: {}", question);
        log.debug("RETRIEVED CHUNKS:");

        int rank = 1;

        for (ScoredChunk result : results) {

            log.debug(
                    "{}. Source: {} | Similarity: {}",
                    rank++,
                    result.chunk().source(),
                    String.format("%.4f", result.score())
            );

            log.debug(
                    "Chunk content: {}",
                    result.chunk().content()
            );
        }

        log.debug("========================================");

        // STEP 6: Return retrieved knowledge to Qwen
        return results.stream()

                .map(result ->
                        "SOURCE: " + result.chunk().source()
                        + "\nSIMILARITY: "
                        + String.format("%.4f", result.score())
                        + "\nCONTENT:\n"
                        + result.chunk().content()
                )

                .collect(Collectors.joining(
                        "\n\n--------------------\n\n"
                ));
    }

    // STEP 7: Calculate cosine similarity
    private double cosineSimilarity(
            List<Double> vectorA,
            List<Double> vectorB
    ) {

        if (vectorA == null || vectorB == null
                || vectorA.size() != vectorB.size()
                || vectorA.isEmpty()) {

            throw new IllegalArgumentException(
                    "Embedding dimensions do not match."
            );
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {

            double a = vectorA.get(i);
            double b = vectorB.get(i);

            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Stores a chunk and its similarity score
    private record ScoredChunk(
            DocumentChunk chunk,
            double score
    ) {
    }
}

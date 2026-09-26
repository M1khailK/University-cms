package ua.foxminded.university.services.ingestion.impl;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ua.foxminded.university.services.ingestion.LessonMaterialEmbeddingGenerator;
import ua.foxminded.university.services.ingestion.model.LessonMaterialEmbeddedChunk;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.List;
import java.util.Objects;

@Service
@ConditionalOnProperty(
        name = "spring.ai.model.embedding.text",
        havingValue = "google-genai"
)
public class GoogleGenAiLessonMaterialEmbeddingGeneratorImpl
        implements LessonMaterialEmbeddingGenerator {

    private final EmbeddingModel embeddingModel;
    private final int expectedDimensions;

    public GoogleGenAiLessonMaterialEmbeddingGeneratorImpl(
            EmbeddingModel embeddingModel,
            @Value(
                    "${spring.ai.google.genai.embedding."
                            + "text.options.dimensions:768}"
            )
            int expectedDimensions
    ) {
        this.embeddingModel = Objects.requireNonNull(
                embeddingModel,
                "Embedding model must not be null."
        );

        if (expectedDimensions < 1) {
            throw new IllegalArgumentException(
                    "Expected embedding dimensions must be positive."
            );
        }

        this.expectedDimensions = expectedDimensions;
    }

    @Override
    public List<LessonMaterialEmbeddedChunk> generate(
            List<LessonMaterialTextChunk> chunks
    ) {
        validateChunks(chunks);

        List<String> texts = chunks.stream()
                .map(LessonMaterialTextChunk::text)
                .toList();

        List<float[]> embeddings = Objects.requireNonNull(
                embeddingModel.embed(texts),
                "Embedding model response must not be null."
        );

        if (embeddings.size() != chunks.size()) {
            throw new IllegalStateException(
                    "Embedding count does not match chunk count."
            );
        }

        return java.util.stream.IntStream
                .range(0, chunks.size())
                .mapToObj(index -> toEmbeddedChunk(
                        chunks.get(index),
                        embeddings.get(index),
                        index
                ))
                .toList();
    }

    private void validateChunks(
            List<LessonMaterialTextChunk> chunks
    ) {
        Objects.requireNonNull(
                chunks,
                "Lesson material chunks must not be null."
        );

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lesson material chunks must not be empty."
            );
        }

        for (LessonMaterialTextChunk chunk : chunks) {
            Objects.requireNonNull(
                    chunk,
                    "Lesson material chunk must not be null."
            );
        }
    }

    private LessonMaterialEmbeddedChunk toEmbeddedChunk(
            LessonMaterialTextChunk chunk,
            float[] embedding,
            int index
    ) {
        if (embedding == null) {
            throw new IllegalStateException(
                    "Embedding must not be null for chunk index: "
                            + index
            );
        }

        if (embedding.length != expectedDimensions) {
            throw new IllegalStateException(
                    "Unexpected embedding dimensions for chunk index "
                            + index
                            + ": expected "
                            + expectedDimensions
                            + ", actual "
                            + embedding.length
            );
        }

        return new LessonMaterialEmbeddedChunk(
                chunk,
                embedding
        );
    }
}
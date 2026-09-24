package ua.foxminded.university.services.search.impl;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition
        .ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ua.foxminded.university.repository
        .LessonMaterialChunkSearchRepository;
import ua.foxminded.university.repository.model
        .LessonMaterialChunkSearchResult;
import ua.foxminded.university.services
        .LessonMaterialAccessService;
import ua.foxminded.university.services.search
        .LessonMaterialSemanticSearchService;
import ua.foxminded.university.services.search.model
        .LessonMaterialSearchResult;

import java.util.List;
import java.util.Objects;

@Service
@ConditionalOnProperty(
        name = "spring.ai.model.embedding.text",
        havingValue = "google-genai"
)
public class LessonMaterialSemanticSearchServiceImpl
        implements LessonMaterialSemanticSearchService {

    private static final int MAX_QUERY_LENGTH = 2_000;
    private static final int MAX_RESULT_LIMIT = 20;

    private final LessonMaterialAccessService accessService;
    private final LessonMaterialChunkSearchRepository searchRepository;
    private final EmbeddingModel embeddingModel;
    private final int expectedDimensions;

    public LessonMaterialSemanticSearchServiceImpl(
            LessonMaterialAccessService accessService,
            LessonMaterialChunkSearchRepository searchRepository,
            EmbeddingModel embeddingModel,
            @Value(
                    "${spring.ai.google.genai.embedding."
                            + "text.options.dimensions:768}"
            )
            int expectedDimensions
    ) {
        this.accessService = Objects.requireNonNull(
                accessService,
                "Lesson material access service must not be null."
        );

        this.searchRepository = Objects.requireNonNull(
                searchRepository,
                "Lesson material search repository must not be null."
        );

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
    public List<LessonMaterialSearchResult> search(
            String query,
            String authenticatedEmail,
            String authenticatedRole,
            int limit
    ) {
        String normalizedQuery = validateQuery(query);
        validateLimit(limit);

        List<Integer> accessibleLessonIds =
                accessService.findAccessibleLessonIds(
                        authenticatedEmail,
                        authenticatedRole
                );

        if (accessibleLessonIds.isEmpty()) {
            return List.of();
        }

        float[] queryEmbedding = Objects.requireNonNull(
                embeddingModel.embed(normalizedQuery),
                "Embedding model response must not be null."
        );

        validateEmbedding(queryEmbedding);

        return searchRepository.findNearest(
                        accessibleLessonIds,
                        queryEmbedding,
                        limit
                )
                .stream()
                .map(this::toSearchResult)
                .toList();
    }

    private String validateQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query must not be blank."
            );
        }

        String normalizedQuery = query.strip();

        if (normalizedQuery.length() > MAX_QUERY_LENGTH) {
            throw new IllegalArgumentException(
                    "Search query must not exceed "
                            + MAX_QUERY_LENGTH
                            + " characters."
            );
        }

        return normalizedQuery;
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > MAX_RESULT_LIMIT) {
            throw new IllegalArgumentException(
                    "Search result limit must be between 1 and "
                            + MAX_RESULT_LIMIT
                            + "."
            );
        }
    }

    private void validateEmbedding(float[] embedding) {
        if (embedding.length != expectedDimensions) {
            throw new IllegalStateException(
                    "Unexpected query embedding dimensions: expected "
                            + expectedDimensions
                            + ", actual "
                            + embedding.length
            );
        }

        for (float value : embedding) {
            if (!Float.isFinite(value)) {
                throw new IllegalStateException(
                        "Query embedding contains a non-finite value."
                );
            }
        }
    }

    private LessonMaterialSearchResult toSearchResult(
            LessonMaterialChunkSearchResult source
    ) {
        return new LessonMaterialSearchResult(
                source.chunkId(),
                source.materialId(),
                source.lessonId(),
                source.originalFilename(),
                source.pageNumber(),
                source.chunkIndex(),
                source.text(),
                source.distance()
        );
    }
}
package ua.foxminded.university.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam
        .NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.repository
        .LessonMaterialChunkSearchRepository;
import ua.foxminded.university.repository.model
        .LessonMaterialChunkSearchResult;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Repository
@RequiredArgsConstructor
public class LessonMaterialChunkSearchRepositoryImpl
        implements LessonMaterialChunkSearchRepository {

    private static final int EMBEDDING_DIMENSIONS = 768;
    private static final int MAX_RESULT_LIMIT = 20;

    private static final String FIND_NEAREST_SQL = """
            SELECT chunk.chunk_id,
                   material.material_id,
                   material.lesson_id,
                   material.original_filename,
                   chunk.page_number,
                   chunk.chunk_index,
                   chunk.chunk_text,
                   chunk.embedding
                       <=> CAST(:queryEmbedding AS vector) AS distance
            FROM lesson_material_chunks chunk
            JOIN lesson_materials material
              ON material.material_id = chunk.material_id
            WHERE material.status = 'READY'
              AND chunk.embedding IS NOT NULL
              AND (
                    chunk.embedding
                        <=> CAST(:queryEmbedding AS vector)
                  ) <= :maxDistance
              AND material.lesson_id IN (:lessonIds)
            ORDER BY chunk.embedding
                         <=> CAST(:queryEmbedding AS vector),
                     chunk.chunk_id
            LIMIT :resultLimit
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<LessonMaterialChunkSearchResult> findNearest(
            Collection<Integer> lessonIds,
            float[] queryEmbedding,
            double maxDistance,
            int limit
    ) {
        Objects.requireNonNull(
                lessonIds,
                "Lesson IDs must not be null."
        );

        validateQueryEmbedding(queryEmbedding);
        validateMaxDistance(maxDistance);
        validateLimit(limit);

        if (lessonIds.isEmpty()) {
            return List.of();
        }

        List<Integer> distinctLessonIds =
                validateAndNormalizeLessonIds(lessonIds);

        MapSqlParameterSource parameters =
                new MapSqlParameterSource()
                        .addValue(
                                "lessonIds",
                                distinctLessonIds
                        )
                        .addValue(
                                "queryEmbedding",
                                toVectorLiteral(queryEmbedding)
                        )
                        .addValue(
                                "maxDistance",
                                maxDistance
                        )
                        .addValue(
                                "resultLimit",
                                limit
                        );

        return jdbcTemplate.query(
                FIND_NEAREST_SQL,
                parameters,
                (resultSet, rowNumber) ->
                        new LessonMaterialChunkSearchResult(
                                resultSet.getLong("chunk_id"),
                                resultSet.getInt("material_id"),
                                resultSet.getInt("lesson_id"),
                                resultSet.getString(
                                        "original_filename"
                                ),
                                resultSet.getInt("page_number"),
                                resultSet.getInt("chunk_index"),
                                resultSet.getString("chunk_text"),
                                resultSet.getDouble("distance")
                        )
        );
    }

    private List<Integer> validateAndNormalizeLessonIds(
            Collection<Integer> lessonIds
    ) {
        for (Integer lessonId : lessonIds) {
            if (lessonId == null || lessonId < 1) {
                throw new IllegalArgumentException(
                        "Every lesson ID must be positive."
                );
            }
        }

        return lessonIds.stream()
                .distinct()
                .toList();
    }

    private void validateQueryEmbedding(float[] queryEmbedding) {
        Objects.requireNonNull(
                queryEmbedding,
                "Query embedding must not be null."
        );

        if (queryEmbedding.length != EMBEDDING_DIMENSIONS) {
            throw new IllegalArgumentException(
                    "Query embedding must contain exactly "
                            + EMBEDDING_DIMENSIONS
                            + " dimensions."
            );
        }

        for (float value : queryEmbedding) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Query embedding must contain only finite values."
                );
            }
        }
    }

    private void validateMaxDistance(double maxDistance) {
        if (!Double.isFinite(maxDistance)
                || maxDistance < 0.0
                || maxDistance > 2.0) {
            throw new IllegalArgumentException(
                    "Maximum cosine distance must be "
                            + "between 0.0 and 2.0."
            );
        }
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

    private String toVectorLiteral(float[] embedding) {
        StringJoiner vector = new StringJoiner(",", "[", "]");

        for (float value : embedding) {
            vector.add(Float.toString(value));
        }

        return vector.toString();
    }
}
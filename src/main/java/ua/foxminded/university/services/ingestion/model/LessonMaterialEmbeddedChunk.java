package ua.foxminded.university.services.ingestion.model;

import java.util.Objects;

public record LessonMaterialEmbeddedChunk(
        LessonMaterialTextChunk chunk,
        float[] embedding
) {

    public LessonMaterialEmbeddedChunk {
        Objects.requireNonNull(
                chunk,
                "Lesson material text chunk must not be null."
        );

        Objects.requireNonNull(
                embedding,
                "Embedding must not be null."
        );

        if (embedding.length == 0) {
            throw new IllegalArgumentException(
                    "Embedding must not be empty."
            );
        }

        float[] defensiveCopy = embedding.clone();

        for (float value : defensiveCopy) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "Embedding must contain only finite values."
                );
            }
        }

        embedding = defensiveCopy;
    }

    @Override
    public float[] embedding() {
        return embedding.clone();
    }
}
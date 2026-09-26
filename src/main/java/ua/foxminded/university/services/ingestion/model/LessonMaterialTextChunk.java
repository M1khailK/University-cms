package ua.foxminded.university.services.ingestion.model;

public record LessonMaterialTextChunk(
        int materialId,
        int pageNumber,
        int chunkIndex,
        String text
) {

    public LessonMaterialTextChunk {
        if (materialId < 1) {
            throw new IllegalArgumentException(
                    "Material ID must be positive."
            );
        }

        if (pageNumber < 1) {
            throw new IllegalArgumentException(
                    "Page number must be positive."
            );
        }

        if (chunkIndex < 0) {
            throw new IllegalArgumentException(
                    "Chunk index must not be negative."
            );
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Chunk text must not be blank."
            );
        }
    }
}
package ua.foxminded.university.services.ingestion.model;

public record LessonMaterialProcessingTarget(
        int materialId,
        String objectKey,
        String versionId
) {

    public LessonMaterialProcessingTarget {
        if (materialId < 1) {
            throw new IllegalArgumentException(
                    "Material ID must be positive."
            );
        }

        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank."
            );
        }

        if (versionId == null || versionId.isBlank()) {
            throw new IllegalArgumentException(
                    "Version ID must not be blank."
            );
        }
    }
}
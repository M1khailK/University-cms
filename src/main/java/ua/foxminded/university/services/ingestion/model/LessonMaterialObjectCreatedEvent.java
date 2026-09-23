package ua.foxminded.university.services.ingestion.model;

public record LessonMaterialObjectCreatedEvent(
        String objectKey,
        long sizeBytes,
        String versionId,
        String sequencer
) {
}
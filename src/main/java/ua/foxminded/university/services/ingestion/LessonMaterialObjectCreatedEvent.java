package ua.foxminded.university.services.ingestion;

public record LessonMaterialObjectCreatedEvent(
        String objectKey,
        long sizeBytes,
        String versionId,
        String sequencer
) {
}
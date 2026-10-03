package ua.foxminded.university.api.lesson.dto;

import ua.foxminded.university.info.LessonMaterialStatus;

import java.time.Instant;

public record LessonMaterialStatusResponse(
        int materialId,
        int lessonId,
        String originalFilename,
        String contentType,
        LessonMaterialStatus status,
        long expectedSizeBytes,
        Long actualSizeBytes,
        Instant createdAt,
        Instant uploadedAt,
        Instant processingStartedAt,
        Instant processedAt,
        String failureReason
) {
}
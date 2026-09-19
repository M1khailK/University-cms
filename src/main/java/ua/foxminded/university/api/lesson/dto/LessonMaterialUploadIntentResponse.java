package ua.foxminded.university.api.lesson.dto;

import java.time.Instant;

public record LessonMaterialUploadIntentResponse(
        Integer materialId,
        String uploadUrl,
        Instant expiresAt,
        String contentType
) {
}
package ua.foxminded.university.api.lesson.dto;

import java.time.Instant;
import java.util.Map;

public record LessonMaterialUploadIntentResponse(
        Integer materialId,
        String uploadUrl,
        String uploadMethod,
        Instant expiresAt,
        String contentType,
        Map<String, String> formFields
) {
}
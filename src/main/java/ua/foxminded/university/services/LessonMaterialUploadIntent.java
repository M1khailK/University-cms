package ua.foxminded.university.services;

import java.net.URL;
import java.time.Instant;
import java.util.Map;

public record LessonMaterialUploadIntent(
        Integer materialId,
        URL uploadUrl,
        String uploadMethod,
        Instant expiresAt,
        String contentType,
        Map<String, String> formFields
) {
}
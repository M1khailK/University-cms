package ua.foxminded.university.services;

import java.net.URL;
import java.time.Instant;

public record LessonMaterialUploadIntent(
        Integer materialId,
        URL uploadUrl,
        Instant expiresAt,
        String contentType
) {
}
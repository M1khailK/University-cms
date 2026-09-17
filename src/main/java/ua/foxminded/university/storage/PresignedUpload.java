package ua.foxminded.university.storage;

import java.net.URL;
import java.time.Instant;

public record PresignedUpload(
        URL url,
        Instant expiresAt,
        String contentType
) {
}
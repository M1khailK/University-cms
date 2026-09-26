package ua.foxminded.university.storage;

import java.net.URL;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record PresignedUpload(
        URL url,
        String method,
        Instant expiresAt,
        String contentType,
        Map<String, String> formFields
) {

    public PresignedUpload {
        Objects.requireNonNull(url, "Upload URL must not be null.");
        Objects.requireNonNull(
                expiresAt,
                "Upload expiration must not be null."
        );
        Objects.requireNonNull(
                formFields,
                "Upload form fields must not be null."
        );

        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException(
                    "Upload method must not be blank."
            );
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Upload content type must not be blank."
            );
        }

        method = method.strip();
        contentType = contentType.strip();
        formFields = Map.copyOf(formFields);
    }
}
package ua.foxminded.university.storage;

public record StoredObjectMetadata(
        long sizeBytes,
        String contentType,
        String versionId
) {
}
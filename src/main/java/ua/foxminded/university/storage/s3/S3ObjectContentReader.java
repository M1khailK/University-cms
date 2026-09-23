package ua.foxminded.university.storage.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import ua.foxminded.university.storage.ObjectContentReader;

import java.util.Objects;

public final class S3ObjectContentReader implements ObjectContentReader {

    private final S3Client s3Client;
    private final String bucket;

    public S3ObjectContentReader(S3Client s3Client, String bucket) {
        this.s3Client = Objects.requireNonNull(s3Client);

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("S3 bucket must not be blank.");
        }

        this.bucket = bucket;
    }

    @Override
    public byte[] read(String objectKey, String versionId) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank."
            );
        }

        if (versionId == null || versionId.isBlank()) {
            throw new IllegalArgumentException(
                    "S3 version ID must not be blank."
            );
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .versionId(versionId)
                .build();

        return s3Client.getObjectAsBytes(request).asByteArray();
    }
}
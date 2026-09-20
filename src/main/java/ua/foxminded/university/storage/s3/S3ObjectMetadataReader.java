package ua.foxminded.university.storage.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import ua.foxminded.university.storage.ObjectMetadataReader;
import ua.foxminded.university.storage.StoredObjectMetadata;

import java.util.Objects;

public final class S3ObjectMetadataReader implements ObjectMetadataReader {

    private final S3Client s3Client;
    private final String bucket;

    public S3ObjectMetadataReader(
            S3Client s3Client,
            String bucket
    ) {
        this.s3Client = Objects.requireNonNull(s3Client);

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException(
                    "S3 bucket must not be blank."
            );
        }

        this.bucket = bucket;
    }

    @Override
    public StoredObjectMetadata read(
            String objectKey,
            String versionId
    ) {
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

        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .versionId(versionId)
                .build();

        HeadObjectResponse response =
                s3Client.headObject(request);

        return new StoredObjectMetadata(
                response.contentLength(),
                response.contentType(),
                response.versionId()
        );
    }
}
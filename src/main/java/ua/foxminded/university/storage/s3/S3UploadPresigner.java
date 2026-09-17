package ua.foxminded.university.storage.s3;

import io.awspring.cloud.s3.S3Operations;
import ua.foxminded.university.storage.PresignedUpload;
import ua.foxminded.university.storage.UploadPresigner;

import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class S3UploadPresigner implements UploadPresigner {

    private final S3Operations s3Operations;
    private final String bucket;
    private final Duration uploadUrlTtl;
    private final Clock clock;

    public S3UploadPresigner(
            S3Operations s3Operations,
            String bucket,
            Duration uploadUrlTtl,
            Clock clock
    ) {
        this.s3Operations = Objects.requireNonNull(s3Operations);
        this.clock = Objects.requireNonNull(clock);

        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("S3 bucket must not be blank.");
        }

        if (uploadUrlTtl == null
                || uploadUrlTtl.isZero()
                || uploadUrlTtl.isNegative()) {
            throw new IllegalArgumentException(
                    "Upload URL TTL must be positive."
            );
        }

        this.bucket = bucket;
        this.uploadUrlTtl = uploadUrlTtl;
    }

    @Override
    public PresignedUpload createUpload(
            String objectKey,
            String contentType
    ) {
        URL signedUrl = s3Operations.createSignedPutURL(
                bucket,
                objectKey,
                uploadUrlTtl,
                null,
                contentType
        );

        Instant expiresAt =
                Instant.now(clock).plus(uploadUrlTtl);

        return new PresignedUpload(
                signedUrl,
                expiresAt,
                contentType
        );
    }
}
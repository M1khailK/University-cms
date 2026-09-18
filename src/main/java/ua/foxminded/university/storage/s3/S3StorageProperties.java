package ua.foxminded.university.storage.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.storage.s3")
public record S3StorageProperties(
        String bucket,
        Duration uploadUrlTtl
) {
}
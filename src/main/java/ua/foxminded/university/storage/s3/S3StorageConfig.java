package ua.foxminded.university.storage.s3;

import io.awspring.cloud.s3.S3Operations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import ua.foxminded.university.customexceptions.StorageUnavailableException;
import ua.foxminded.university.storage.ObjectMetadataReader;
import ua.foxminded.university.storage.UploadPresigner;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {

    @Bean
    @ConditionalOnProperty(
            name = "spring.cloud.aws.s3.enabled",
            havingValue = "true"
    )
    public ObjectMetadataReader objectMetadataReader(
            S3Client s3Client,
            S3StorageProperties properties
    ) {
        return new S3ObjectMetadataReader(
                s3Client,
                properties.bucket()
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "spring.cloud.aws.s3.enabled",
            havingValue = "false",
            matchIfMissing = true
    )
    public ObjectMetadataReader unavailableObjectMetadataReader() {
        return (objectKey, versionId) -> {
            throw new StorageUnavailableException(
                    "File storage is currently unavailable."
            );
        };
    }

    @Bean
    @ConditionalOnProperty(
            name = "spring.cloud.aws.s3.enabled",
            havingValue = "true"
    )
    public UploadPresigner uploadPresigner(
            S3Operations s3Operations,
            S3StorageProperties properties,
            Clock clock
    ) {
        return new S3UploadPresigner(
                s3Operations,
                properties.bucket(),
                properties.uploadUrlTtl(),
                clock
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "spring.cloud.aws.s3.enabled",
            havingValue = "false",
            matchIfMissing = true
    )
    public UploadPresigner unavailableUploadPresigner() {
        return (objectKey, contentType) -> {
            throw new StorageUnavailableException(
                    "File storage is currently unavailable."
            );
        };
    }
}
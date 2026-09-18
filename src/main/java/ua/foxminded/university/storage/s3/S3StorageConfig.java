package ua.foxminded.university.storage.s3;

import io.awspring.cloud.s3.S3Operations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.foxminded.university.storage.UploadPresigner;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
@ConditionalOnProperty(
        name = "spring.cloud.aws.s3.enabled",
        havingValue = "true"
)
public class S3StorageConfig {

    @Bean
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
}
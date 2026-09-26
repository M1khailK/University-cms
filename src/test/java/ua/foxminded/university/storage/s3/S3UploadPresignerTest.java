package ua.foxminded.university.storage.s3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import ua.foxminded.university.storage.PresignedUpload;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class S3UploadPresignerTest {

    private static final String BUCKET =
            "university-cms-materials";

    private static final String OBJECT_KEY =
            "lesson-materials/17/material-id";

    private static final String CONTENT_TYPE =
            "application/pdf";

    private static final long EXPECTED_SIZE_BYTES = 1_024L;

    private static final Duration TTL =
            Duration.ofMinutes(10);

    private static final Instant NOW =
            Instant.parse("2026-09-17T17:00:00Z");

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Test
    void createUpload_shouldCreatePostPolicyWithExactSize()
            throws Exception {

        AwsSessionCredentials credentials =
                AwsSessionCredentials.create(
                        "test-access-key",
                        "test-secret-key",
                        "test-session-token"
                );

        S3UploadPresigner presigner =
                new S3UploadPresigner(
                        objectMapper,
                        StaticCredentialsProvider.create(
                                credentials
                        ),
                        () -> Region.EU_CENTRAL_1,
                        BUCKET,
                        TTL,
                        Clock.fixed(NOW, ZoneOffset.UTC)
                );

        PresignedUpload actual =
                presigner.createUpload(
                        OBJECT_KEY,
                        CONTENT_TYPE,
                        EXPECTED_SIZE_BYTES
                );

        assertEquals(
                "https://university-cms-materials"
                        + ".s3.eu-central-1.amazonaws.com/",
                actual.url().toString()
        );

        assertEquals("POST", actual.method());
        assertEquals(NOW.plus(TTL), actual.expiresAt());
        assertEquals(CONTENT_TYPE, actual.contentType());

        assertEquals(
                OBJECT_KEY,
                actual.formFields().get("key")
        );

        assertEquals(
                CONTENT_TYPE,
                actual.formFields().get("Content-Type")
        );

        assertEquals(
                "AWS4-HMAC-SHA256",
                actual.formFields().get("x-amz-algorithm")
        );

        assertEquals(
                "test-session-token",
                actual.formFields().get(
                        "x-amz-security-token"
                )
        );

        assertTrue(
                actual.formFields()
                        .get("x-amz-signature")
                        .matches("[0-9a-f]{64}")
        );

        String decodedPolicy = new String(
                Base64.getDecoder().decode(
                        actual.formFields().get("policy")
                ),
                StandardCharsets.UTF_8
        );

        JsonNode policy =
                objectMapper.readTree(decodedPolicy);

        assertEquals(
                "2026-09-17T17:10:00Z",
                policy.path("expiration").asText()
        );

        assertTrue(
                containsExactSizeCondition(
                        policy.path("conditions"),
                        EXPECTED_SIZE_BYTES
                )
        );
    }

    @Test
    void createUpload_shouldRejectNonPositiveSize() {
        S3UploadPresigner presigner =
                new S3UploadPresigner(
                        objectMapper,
                        StaticCredentialsProvider.create(
                                AwsSessionCredentials.create(
                                        "test-access-key",
                                        "test-secret-key",
                                        "test-session-token"
                                )
                        ),
                        () -> Region.EU_CENTRAL_1,
                        BUCKET,
                        TTL,
                        Clock.fixed(NOW, ZoneOffset.UTC)
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> presigner.createUpload(
                        OBJECT_KEY,
                        CONTENT_TYPE,
                        0L
                )
        );
    }

    private boolean containsExactSizeCondition(
            JsonNode conditions,
            long expectedSizeBytes
    ) {
        for (JsonNode condition : conditions) {
            if (condition.isArray()
                    && condition.size() == 3
                    && "content-length-range".equals(
                    condition.get(0).asText()
            )
                    && condition.get(1).asLong()
                    == expectedSizeBytes
                    && condition.get(2).asLong()
                    == expectedSizeBytes) {
                return true;
            }
        }

        return false;
    }
}
package ua.foxminded.university.storage.s3;

import io.awspring.cloud.s3.S3Operations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.storage.PresignedUpload;

import java.net.URI;
import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3UploadPresignerTest {

    private static final String BUCKET =
            "university-cms-materials";

    private static final String OBJECT_KEY =
            "lesson-materials/17/material-id";

    private static final String CONTENT_TYPE =
            "application/pdf";

    private static final Duration TTL =
            Duration.ofMinutes(10);

    private static final Instant NOW =
            Instant.parse("2026-09-17T17:00:00Z");

    @Mock
    private S3Operations s3Operations;

    @Test
    public void createUpload_shouldReturnSignedUploadData()
            throws Exception {

        URL signedUrl = URI.create(
                "https://example.s3.amazonaws.com/signed-upload"
        ).toURL();

        when(s3Operations.createSignedPutURL(
                BUCKET,
                OBJECT_KEY,
                TTL,
                null,
                CONTENT_TYPE
        )).thenReturn(signedUrl);

        Clock clock =
                Clock.fixed(NOW, ZoneOffset.UTC);

        S3UploadPresigner presigner =
                new S3UploadPresigner(
                        s3Operations,
                        BUCKET,
                        TTL,
                        clock
                );

        PresignedUpload actual =
                presigner.createUpload(
                        OBJECT_KEY,
                        CONTENT_TYPE
                );

        Assertions.assertEquals(signedUrl, actual.url());
        Assertions.assertEquals(
                NOW.plus(TTL),
                actual.expiresAt()
        );
        Assertions.assertEquals(
                CONTENT_TYPE,
                actual.contentType()
        );

        verify(s3Operations).createSignedPutURL(
                BUCKET,
                OBJECT_KEY,
                TTL,
                null,
                CONTENT_TYPE
        );
    }
}
package ua.foxminded.university.storage.s3;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import ua.foxminded.university.storage.StoredObjectMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3ObjectMetadataReaderTest {

    @Test
    void read_shouldHeadExactObjectVersion() {
        S3Client s3Client = mock(S3Client.class);

        HeadObjectResponse response =
                HeadObjectResponse.builder()
                        .contentLength(164293L)
                        .contentType("application/pdf")
                        .versionId("version-123")
                        .build();

        when(s3Client.headObject(
                org.mockito.ArgumentMatchers.any(HeadObjectRequest.class)
        )).thenReturn(response);

        S3ObjectMetadataReader reader =
                new S3ObjectMetadataReader(
                        s3Client,
                        "lesson-materials-bucket"
                );

        StoredObjectMetadata metadata = reader.read(
                "lesson-materials/1/object-123",
                "version-123"
        );

        assertEquals(164293L, metadata.sizeBytes());
        assertEquals(
                "application/pdf",
                metadata.contentType()
        );
        assertEquals(
                "version-123",
                metadata.versionId()
        );

        ArgumentCaptor<HeadObjectRequest> captor =
                ArgumentCaptor.forClass(HeadObjectRequest.class);

        verify(s3Client).headObject(captor.capture());

        HeadObjectRequest request = captor.getValue();

        assertEquals(
                "lesson-materials-bucket",
                request.bucket()
        );
        assertEquals(
                "lesson-materials/1/object-123",
                request.key()
        );
        assertEquals(
                "version-123",
                request.versionId()
        );
    }
}
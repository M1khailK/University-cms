package ua.foxminded.university.storage.s3;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3ObjectContentReaderTest {

    @Test
    void read_shouldRequestAndReturnExactObjectVersion() {
        S3Client s3Client = mock(S3Client.class);
        byte[] pdfBytes = "%PDF-1.7".getBytes(StandardCharsets.US_ASCII);

        ResponseBytes<GetObjectResponse> response =
                ResponseBytes.fromByteArray(
                        GetObjectResponse.builder()
                                .versionId("version-123")
                                .build(),
                        pdfBytes
                );

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(response);

        S3ObjectContentReader reader =
                new S3ObjectContentReader(
                        s3Client,
                        "lesson-materials-bucket"
                );

        byte[] actual = reader.read(
                "lesson-materials/1/object-123",
                "version-123"
        );

        assertArrayEquals(pdfBytes, actual);

        ArgumentCaptor<GetObjectRequest> captor =
                ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObjectAsBytes(captor.capture());

        GetObjectRequest request = captor.getValue();
        assertEquals("lesson-materials-bucket", request.bucket());
        assertEquals("lesson-materials/1/object-123", request.key());
        assertEquals("version-123", request.versionId());
    }
}
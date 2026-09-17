package ua.foxminded.university.storage;

public interface UploadPresigner {

    PresignedUpload createUpload(
            String objectKey,
            String contentType
    );
}
package ua.foxminded.university.storage;

public interface ObjectMetadataReader {

    StoredObjectMetadata read(
            String objectKey,
            String versionId
    );
}
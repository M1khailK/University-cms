package ua.foxminded.university.storage;

public interface ObjectContentReader {

    byte[] read(String objectKey, String versionId);
}
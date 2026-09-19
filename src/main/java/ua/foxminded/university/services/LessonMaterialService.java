package ua.foxminded.university.services;

public interface LessonMaterialService {

    LessonMaterialUploadIntent createUploadIntent(
            int lessonId,
            String authenticatedEmail,
            String originalFilename,
            String contentType,
            long expectedSizeBytes
    );
}
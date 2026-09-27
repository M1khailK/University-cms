package ua.foxminded.university.services;

public interface LessonMaterialService {

    LessonMaterialStatusDetails getMaterialStatus(
            int lessonId,
            int materialId,
            String authenticatedEmail
    );

    LessonMaterialUploadIntent createUploadIntent(
            int lessonId,
            String authenticatedEmail,
            String originalFilename,
            String contentType,
            long expectedSizeBytes
    );
}
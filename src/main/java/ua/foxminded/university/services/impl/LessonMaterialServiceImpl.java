package ua.foxminded.university.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.LessonMaterialService;
import ua.foxminded.university.services.LessonMaterialUploadIntent;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.storage.PresignedUpload;
import ua.foxminded.university.storage.UploadPresigner;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonMaterialServiceImpl implements LessonMaterialService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;

    private final LessonMaterialRepository lessonMaterialRepository;
    private final LessonService lessonService;
    private final TeacherService teacherService;
    private final UploadPresigner uploadPresigner;
    private final Clock clock;

    @Override
    @Transactional
    public LessonMaterialUploadIntent createUploadIntent(
            int lessonId,
            String authenticatedEmail,
            String originalFilename,
            String contentType,
            long expectedSizeBytes
    ) {
        validateUploadRequest(
                authenticatedEmail,
                originalFilename,
                contentType,
                expectedSizeBytes
        );

        Teacher teacher = teacherService.getByEmail(authenticatedEmail);
        Lesson lesson = lessonService.getById(lessonId);

        ensureTeacherOwnsLesson(teacher, lesson);

        String objectKey = createObjectKey(lessonId);

        LessonMaterial material = new LessonMaterial();
        material.setLesson(lesson);
        material.setObjectKey(objectKey);
        material.setOriginalFilename(originalFilename.strip());
        material.setContentType(PDF_CONTENT_TYPE);
        material.setExpectedSizeBytes(expectedSizeBytes);
        material.setStatus(LessonMaterialStatus.PENDING_UPLOAD);
        material.setCreatedAt(Instant.now(clock));

        LessonMaterial saved =
                lessonMaterialRepository.saveAndFlush(material);

        PresignedUpload presignedUpload =
                uploadPresigner.createUpload(
                        objectKey,
                        PDF_CONTENT_TYPE
                );

        return new LessonMaterialUploadIntent(
                saved.getId(),
                presignedUpload.url(),
                presignedUpload.expiresAt(),
                presignedUpload.contentType()
        );
    }

    private void validateUploadRequest(
            String authenticatedEmail,
            String originalFilename,
            String contentType,
            long expectedSizeBytes
    ) {
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated email must not be blank."
            );
        }

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException(
                    "Original filename must not be blank."
            );
        }

        if (originalFilename.length() > 255) {
            throw new IllegalArgumentException(
                    "Original filename must not exceed 255 characters."
            );
        }

        if (contentType == null
                || !PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException(
                    "Only PDF files are supported."
            );
        }

        if (expectedSizeBytes <= 0) {
            throw new IllegalArgumentException(
                    "File size must be positive."
            );
        }

        if (expectedSizeBytes > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "File size must not exceed 10 MB."
            );
        }
    }

    private void ensureTeacherOwnsLesson(
            Teacher authenticatedTeacher,
            Lesson lesson
    ) {
        Teacher lessonTeacher = lesson.getTeacher();

        if (lessonTeacher == null
                || !Objects.equals(
                authenticatedTeacher.getId(),
                lessonTeacher.getId()
        )) {
            throw new AccessDeniedException(
                    "You are not allowed to upload materials for this lesson."
            );
        }
    }

    private String createObjectKey(int lessonId) {
        return "lesson-materials/"
                + lessonId
                + "/"
                + UUID.randomUUID();
    }
}
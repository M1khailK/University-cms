package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ua.foxminded.university.customexceptions.InvalidLessonMaterialUploadException;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.LessonMaterialUploadIntent;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.storage.PresignedUpload;
import ua.foxminded.university.storage.UploadPresigner;

import java.net.URI;
import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LessonMaterialServiceImplTest {

    private static final String EMAIL =
            "teacher@university.com";

    private static final Instant NOW =
            Instant.parse("2026-09-19T12:00:00Z");

    @Mock
    private LessonMaterialRepository lessonMaterialRepository;

    @Mock
    private LessonService lessonService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private UploadPresigner uploadPresigner;

    private LessonMaterialServiceImpl lessonMaterialService;

    @BeforeEach
    public void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

        lessonMaterialService =
                new LessonMaterialServiceImpl(
                        lessonMaterialRepository,
                        lessonService,
                        teacherService,
                        uploadPresigner,
                        clock
                );
    }

    @Test
    public void createUploadIntent_shouldCreatePendingMaterial_whenTeacherOwnsLesson()
            throws Exception {

        Teacher teacher = createTeacher(7);
        Lesson lesson = createLesson(17, teacher);

        when(teacherService.getByEmail(EMAIL))
                .thenReturn(teacher);

        when(lessonService.getById(17))
                .thenReturn(lesson);

        when(lessonMaterialRepository.saveAndFlush(
                any(LessonMaterial.class)
        )).thenAnswer(invocation -> {
            LessonMaterial material = invocation.getArgument(0);
            material.setId(42);
            return material;
        });

        URL signedUrl = URI.create(
                "https://example.com/signed-upload"
        ).toURL();

        Instant expiresAt =
                Instant.parse("2026-09-19T12:10:00Z");

        when(uploadPresigner.createUpload(
                anyString(),
                anyString(),
                anyLong()
        )).thenReturn(
                new PresignedUpload(
                        signedUrl,
                        "POST",
                        expiresAt,
                        "application/pdf",
                        Map.of(
                                "key",
                                "lesson-materials/17/material-id",
                                "policy",
                                "encoded-policy",
                                "x-amz-signature",
                                "signature"
                        )
                )
        );

        LessonMaterialUploadIntent result =
                lessonMaterialService.createUploadIntent(
                        17,
                        EMAIL,
                        "lecture.pdf",
                        "application/pdf",
                        1024L
                );

        ArgumentCaptor<LessonMaterial> materialCaptor =
                ArgumentCaptor.forClass(LessonMaterial.class);

        verify(lessonMaterialRepository)
                .saveAndFlush(materialCaptor.capture());

        LessonMaterial saved =
                materialCaptor.getValue();

        assertEquals(lesson, saved.getLesson());
        assertEquals("lecture.pdf", saved.getOriginalFilename());
        assertEquals("application/pdf", saved.getContentType());
        assertEquals(1024L, saved.getExpectedSizeBytes());
        assertEquals(
                LessonMaterialStatus.PENDING_UPLOAD,
                saved.getStatus()
        );
        assertEquals(NOW, saved.getCreatedAt());
        assertNull(saved.getUploadedAt());
        assertNull(saved.getActualSizeBytes());

        assertTrue(
                saved.getObjectKey()
                        .startsWith("lesson-materials/17/")
        );

        verify(uploadPresigner).createUpload(
                saved.getObjectKey(),
                "application/pdf",
                1024L
        );

        assertEquals(42, result.materialId());
        assertEquals(signedUrl, result.uploadUrl());
        assertEquals(expiresAt, result.expiresAt());
        assertEquals(
                "application/pdf",
                result.contentType()
        );
    }

    @Test
    public void createUploadIntent_shouldDenyUpload_whenTeacherDoesNotOwnLesson() {
        Teacher authenticatedTeacher = createTeacher(7);
        Teacher lessonTeacher = createTeacher(8);

        Lesson lesson = createLesson(17, lessonTeacher);

        when(teacherService.getByEmail(EMAIL))
                .thenReturn(authenticatedTeacher);

        when(lessonService.getById(17))
                .thenReturn(lesson);

        assertThrows(
                AccessDeniedException.class,
                () -> lessonMaterialService.createUploadIntent(
                        17,
                        EMAIL,
                        "lecture.pdf",
                        "application/pdf",
                        1024L
                )
        );

        verifyNoInteractions(
                lessonMaterialRepository,
                uploadPresigner
        );
    }

    @Test
    public void createUploadIntent_shouldRejectUpload_whenContentTypeIsNotPdf() {
        InvalidLessonMaterialUploadException exception =
                assertThrows(
                        InvalidLessonMaterialUploadException.class,
                        () -> lessonMaterialService.createUploadIntent(
                                17,
                                EMAIL,
                                "lecture.txt",
                                "text/plain",
                                1024L
                        )
                );

        assertEquals(
                "Only PDF files are supported.",
                exception.getMessage()
        );

        verifyNoInteractions(
                teacherService,
                lessonService,
                lessonMaterialRepository,
                uploadPresigner
        );
    }

    @Test
    public void createUploadIntent_shouldRejectUpload_whenFileIsTooLarge() {
        long elevenMegabytes =
                11L * 1024 * 1024;
        InvalidLessonMaterialUploadException exception =
                assertThrows(
                        InvalidLessonMaterialUploadException.class,
                        () -> lessonMaterialService.createUploadIntent(
                                17,
                                EMAIL,
                                "lecture.pdf",
                                "application/pdf",
                                elevenMegabytes
                        )
                );

        assertEquals(
                "File size must not exceed 10 MB.",
                exception.getMessage()
        );
        verifyNoInteractions(
                teacherService,
                lessonService,
                lessonMaterialRepository,
                uploadPresigner
        );
    }

    private Teacher createTeacher(int id) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setEmail(EMAIL);
        teacher.setRole("TEACHER");
        return teacher;
    }

    private Lesson createLesson(
            int id,
            Teacher teacher
    ) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setTeacher(teacher);
        return lesson;
    }
}
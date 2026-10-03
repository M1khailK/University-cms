package ua.foxminded.university.api.lesson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.customexceptions.InvalidLessonMaterialUploadException;
import ua.foxminded.university.customexceptions.LessonMaterialNotFoundException;
import ua.foxminded.university.customexceptions.StorageUnavailableException;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.services.LessonMaterialService;
import ua.foxminded.university.services.LessonMaterialStatusDetails;
import ua.foxminded.university.services.LessonMaterialUploadIntent;

import javax.sql.DataSource;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LessonMaterialRestController.class)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
        ApiExceptionHandler.class
})
class LessonMaterialApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonMaterialService lessonMaterialService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void uploadIntent_shouldAllowTeacher() throws Exception {
        LessonMaterialUploadIntent uploadIntent =
                new LessonMaterialUploadIntent(
                        42,
                        URI.create(
                                "https://example.com/signed-upload"
                        ).toURL(),
                        "POST",
                        Instant.parse("2026-09-19T12:10:00Z"),
                        "application/pdf",
                        Map.of(
                                "key",
                                "lesson-materials/17/material-id",
                                "policy",
                                "encoded-policy",
                                "x-amz-signature",
                                "signature"
                        )
                );

        when(lessonMaterialService.createUploadIntent(
                17,
                "teacher@university.com",
                "lecture.pdf",
                "application/pdf",
                1024L
        )).thenReturn(uploadIntent);

        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "originalFilename": "lecture.pdf",
                                          "contentType": "application/pdf",
                                          "expectedSizeBytes": 1024
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.materialId").value(42))
                .andExpect(jsonPath("$.uploadUrl")
                        .value("https://example.com/signed-upload"))
                .andExpect(jsonPath("$.uploadMethod")
                        .value("POST"))
                .andExpect(jsonPath("$.formFields.policy")
                        .value("encoded-policy"))
                .andExpect(jsonPath("$.formFields.x-amz-signature")
                        .value("signature"))
                .andExpect(jsonPath("$.contentType")
                        .value("application/pdf"));

        verify(lessonMaterialService).createUploadIntent(
                17,
                "teacher@university.com",
                "lecture.pdf",
                "application/pdf",
                1024L
        );
    }

    @Test
    void uploadIntent_shouldForbidStudent() throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("student@university.com")
                                        .roles("STUDENT"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadIntent_shouldForbidAdmin() throws Exception {
        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("admin@university.com")
                                        .roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadIntent_shouldReturnUnauthorized_whenAnonymous()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadIntent_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "originalFilename": "",
                                          "contentType": "application/pdf",
                                          "expectedSizeBytes": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"));

        verifyNoInteractions(lessonMaterialService);
    }

    @Test
    void uploadIntent_shouldReturnBadRequest_whenFileTypeIsNotSupported()
            throws Exception {

        when(lessonMaterialService.createUploadIntent(
                17,
                "teacher@university.com",
                "lecture.txt",
                "text/plain",
                1024L
        )).thenThrow(
                new InvalidLessonMaterialUploadException(
                        "Only PDF files are supported."
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "originalFilename": "lecture.txt",
                                          "contentType": "text/plain",
                                          "expectedSizeBytes": 1024
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid lesson material upload"))
                .andExpect(jsonPath("$.detail")
                        .value("Only PDF files are supported."));
    }

    @Test
    void uploadIntent_shouldReturnServiceUnavailable_whenStorageIsUnavailable()
            throws Exception {

        when(lessonMaterialService.createUploadIntent(
                17,
                "teacher@university.com",
                "lecture.pdf",
                "application/pdf",
                1024L
        )).thenThrow(
                new StorageUnavailableException(
                        "File storage is currently unavailable."
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/lessons/17/materials/upload-intent"
                        )
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest())
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title")
                        .value("File storage unavailable"))
                .andExpect(jsonPath("$.detail")
                        .value("File storage is currently unavailable."));
    }

    @Test
    void materialStatus_shouldAllowOwningTeacher() throws Exception {
        LessonMaterialStatusDetails details =
                new LessonMaterialStatusDetails(
                        42,
                        17,
                        "lecture.pdf",
                        "application/pdf",
                        LessonMaterialStatus.READY,
                        2048L,
                        2048L,
                        Instant.parse("2026-09-19T12:00:00Z"),
                        Instant.parse("2026-09-19T12:01:00Z"),
                        Instant.parse("2026-09-19T12:02:00Z"),
                        Instant.parse("2026-09-19T12:03:00Z"),
                        null
                );

        when(lessonMaterialService.getMaterialStatus(
                17,
                42,
                "teacher@university.com"
        )).thenReturn(details);

        mockMvc.perform(
                        get("/api/v1/lessons/17/materials/42")
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materialId").value(42))
                .andExpect(jsonPath("$.lessonId").value(17))
                .andExpect(jsonPath("$.originalFilename")
                        .value("lecture.pdf"))
                .andExpect(jsonPath("$.contentType")
                        .value("application/pdf"))
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.expectedSizeBytes").value(2048))
                .andExpect(jsonPath("$.actualSizeBytes").value(2048))
                .andExpect(jsonPath("$.objectKey").doesNotExist())
                .andExpect(jsonPath("$.checksumSha256").doesNotExist())
                .andExpect(jsonPath("$.s3VersionId").doesNotExist())
                .andExpect(jsonPath("$.s3Sequencer").doesNotExist());

        verify(lessonMaterialService).getMaterialStatus(
                17,
                42,
                "teacher@university.com"
        );
    }

    @Test
    void materialStatus_shouldForbidStudent() throws Exception {
        mockMvc.perform(
                        get("/api/v1/lessons/17/materials/42")
                                .with(user("student@university.com")
                                        .roles("STUDENT"))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(lessonMaterialService);
    }

    @Test
    void materialStatus_shouldReturnUnauthorized_whenAnonymous()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/lessons/17/materials/42")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(lessonMaterialService);
    }

    @Test
    void materialStatus_shouldReturnNotFound_whenMaterialIsUnavailable()
            throws Exception {

        when(lessonMaterialService.getMaterialStatus(
                17,
                42,
                "teacher@university.com"
        )).thenThrow(new LessonMaterialNotFoundException(42));

        mockMvc.perform(
                        get("/api/v1/lessons/17/materials/42")
                                .with(user("teacher@university.com")
                                        .roles("TEACHER"))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Lesson material not found"))
                .andExpect(jsonPath("$.detail")
                        .value("Lesson material was not found by id: 42"));
    }

    private String validRequest() {
        return """
                {
                  "originalFilename": "lecture.pdf",
                  "contentType": "application/pdf",
                  "expectedSizeBytes": 1024
                }
                """;
    }
}
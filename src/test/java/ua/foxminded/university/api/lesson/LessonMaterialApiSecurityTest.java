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
import ua.foxminded.university.services.LessonMaterialService;
import ua.foxminded.university.services.LessonMaterialUploadIntent;
import ua.foxminded.university.customexceptions.InvalidLessonMaterialUploadException;
import ua.foxminded.university.customexceptions.StorageUnavailableException;

import static org.mockito.Mockito.verifyNoInteractions;
import javax.sql.DataSource;
import java.net.URI;
import java.time.Instant;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
                        Instant.parse("2026-09-19T12:10:00Z"),
                        "application/pdf"
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
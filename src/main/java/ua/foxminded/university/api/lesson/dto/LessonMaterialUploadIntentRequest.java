package ua.foxminded.university.api.lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record LessonMaterialUploadIntentRequest(

        @NotBlank
        @Size(max = 255)
        String originalFilename,

        @NotBlank
        @Size(max = 100)
        String contentType,

        @Positive
        @Max(10L * 1024 * 1024)
        long expectedSizeBytes
) {
}
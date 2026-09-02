package ua.foxminded.university.api.grade.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GradeCreateRequest(

        @NotNull(message = "Student id must not be null")
        @Positive(message = "Student id must be positive")
        Integer studentId,

        @NotNull(message = "Lesson id must not be null")
        @Positive(message = "Lesson id must be positive")
        Integer lessonId,

        @NotNull(message = "Grade value must not be null")
        @Min(value = 1, message = "Grade value must be at least 1")
        @Max(value = 5, message = "Grade value must be at most 5")
        Integer value
) {
}
package ua.foxminded.university.api.grade.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GradeCreateRequest(

        @NotNull(message = "Student id must not be null")
        Integer studentId,

        @NotNull(message = "Lesson id must not be null")
        Integer lessonId,

        @NotNull(message = "Grade value must not be null")
        @Min(value = 1, message = "Grade value must be at least 1")
        @Max(value = 5, message = "Grade value must be at most 5")
        Integer value
) {
}
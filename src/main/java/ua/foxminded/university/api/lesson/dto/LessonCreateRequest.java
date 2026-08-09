package ua.foxminded.university.api.lesson.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonCreateRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name,

        @NotNull(message = "Date must not be null")
        LocalDate date,

        @NotNull(message = "Start time must not be null")
        LocalTime startTime,
        @NotNull(message = "End time must not be null")
        LocalTime endTime,

        @NotNull(message = "Subject id must not be null")
        @Positive(message = "Subject id must be positive")
        Integer subjectId,

        @NotNull(message = "Group id must not be null")
        @Positive(message = "Group id must be positive")
        Integer groupId,

        @NotNull(message = "Teacher id must not be null")
        @Positive(message = "Teacher id must be positive")
        Integer teacherId
) {
    @AssertTrue(message = "End time must be after start time")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}

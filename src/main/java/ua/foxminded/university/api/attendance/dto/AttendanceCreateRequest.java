package ua.foxminded.university.api.attendance.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceCreateRequest(

        @NotNull(message = "Student id must not be null")
        @Positive(message = "Student id must be positive")
        Integer studentId,

        @NotNull(message = "Lesson id must not be null")
        @Positive(message = "Lesson id must be positive")
        Integer lessonId,

        @NotNull(message = "Attendance date must not be null")
        LocalDate attendanceDate,

        @NotNull(message = "Attendance time must not be null")
        LocalTime attendanceTime

) {
}
package ua.foxminded.university.api.attendance.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceCreateRequest(

        @NotNull(message = "Student id must not be null")
        Integer studentId,

        @NotNull(message = "Lesson id must not be null")
        Integer lessonId,

        @NotNull(message = "Attendance date must not be null")
        LocalDate attendanceDate,

        @NotNull(message = "Attendance time must not be null")
        LocalTime attendanceTime

) {
}
package ua.foxminded.university.api.attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceResponse(
        Integer id,
        Integer studentId,
        Integer lessonId,
        LocalDate attendanceDate,
        LocalTime attendanceTime
) {
}
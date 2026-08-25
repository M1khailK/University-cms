package ua.foxminded.university.api.grade.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record GradeResponse(
        Integer id,
        Integer value,
        Integer studentId,
        String studentFirstName,
        String studentLastName,
        String studentEmail,
        Integer lessonId,
        String lessonName,
        LocalDate lessonDate,
        LocalTime lessonStartTime,
        LocalTime lessonEndTime
) {
}
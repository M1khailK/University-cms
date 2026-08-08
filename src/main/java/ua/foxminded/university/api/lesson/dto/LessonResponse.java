package ua.foxminded.university.api.lesson.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonResponse(
        Integer id,
        String name,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,

        Integer subjectId,
        String subjectName,

        Integer groupId,
        String groupName,

        Integer teacherId,
        String teacherFirstName,
        String teacherLastName,
        String teacherEmail
) {
}
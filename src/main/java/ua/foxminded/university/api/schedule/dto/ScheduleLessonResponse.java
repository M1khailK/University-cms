package ua.foxminded.university.api.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleLessonResponse(
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
        String teacherLastName
) {
}

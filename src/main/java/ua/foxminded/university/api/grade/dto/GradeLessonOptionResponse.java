package ua.foxminded.university.api.grade.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record GradeLessonOptionResponse(
        Integer id,
        String name,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer groupId,
        String groupName,
        List<GradeStudentOptionResponse> students
) {
}
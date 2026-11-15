package ua.foxminded.university.api.grade.dto;

import java.util.List;

public record GradeOptionsResponse(
        List<GradeLessonOptionResponse> lessons
) {
}
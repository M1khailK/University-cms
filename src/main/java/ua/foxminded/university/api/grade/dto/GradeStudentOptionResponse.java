package ua.foxminded.university.api.grade.dto;

public record GradeStudentOptionResponse(
        Integer id,
        String firstName,
        String lastName,
        String email
) {
}
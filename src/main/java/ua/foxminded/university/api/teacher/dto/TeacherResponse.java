package ua.foxminded.university.api.teacher.dto;

public record TeacherResponse(
        Integer id,
        String firstName,
        String lastName,
        String email
) {
}

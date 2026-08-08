package ua.foxminded.university.api.group.dto;

public record GroupStudentResponse(
        Integer id,
        String firstName,
        String lastName,
        String email
) {
}

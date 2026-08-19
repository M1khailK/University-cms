package ua.foxminded.university.api.student.dto;

public record StudentResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        Integer groupId,
        String groupName
) {
}

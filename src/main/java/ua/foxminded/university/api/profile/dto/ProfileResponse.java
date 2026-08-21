package ua.foxminded.university.api.profile.dto;

public record ProfileResponse(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String role,
        Integer groupId,
        String groupName
) {
}
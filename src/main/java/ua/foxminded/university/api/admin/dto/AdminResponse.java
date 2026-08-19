package ua.foxminded.university.api.admin.dto;

public record AdminResponse(
        Integer id,
        String firstName,
        String lastName,
        String email
) {
}
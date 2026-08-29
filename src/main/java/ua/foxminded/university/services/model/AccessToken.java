package ua.foxminded.university.services.model;

public record AccessToken(
        String value,
        long expiresInSeconds
) {
}
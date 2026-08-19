package ua.foxminded.university.api.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StudentUpdateRequest(
        @NotBlank(message = "First name must not be blank")
        @Size(max = 50, message = "First name must be at most 50 characters")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        String lastName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be vaild")
        @Size(max = 50, message = "Email must be at most 50 characters")
        String email,

        @NotNull(message = "Group id must not be null")
        @Positive(message = "Group id must be positive")
        Integer groupId
) {
}

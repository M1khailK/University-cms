package ua.foxminded.university.api.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfilePasswordUpdateRequest(

        @NotBlank(message = "Old password must not be blank")
        @Size(min = 5, max = 72, message = "Old password must be between 5 and 72 characters")
        String oldPassword,

        @NotBlank(message = "New password must not be blank")
        @Size(min = 5, max = 72, message = "New password must be between 5 and 72 characters")
        String newPassword
) {
}
package ua.foxminded.university.api.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectCreateRequest(

        @NotBlank(message = "Subject name must not be blank")
        @Size(max = 50, message = "Subject name must not exceed 50 characters")
        String name
) {
}
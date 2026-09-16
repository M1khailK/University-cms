package ua.foxminded.university.api.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistantMessageRequest(

        @NotBlank(message = "Message must not be blank")
        @Size(max = 2000, message = "Message must be at most 2000 characters")
        String message

) {
}
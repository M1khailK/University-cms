package ua.foxminded.university.api.assistant;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.assistant.dto.AssistantMessageRequest;
import ua.foxminded.university.api.assistant.dto.AssistantMessageResponse;
import ua.foxminded.university.services.AssistantService;

@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssistantRestController {

    private final AssistantService assistantService;

    @PostMapping("/messages")
    public AssistantMessageResponse sendMessage(
            @Valid @RequestBody AssistantMessageRequest request,
            Authentication authentication
    ) {
        return new AssistantMessageResponse(
                assistantService.answer(request.message(), authentication)
        );
    }
}
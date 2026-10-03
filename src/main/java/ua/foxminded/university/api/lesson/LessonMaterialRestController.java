package ua.foxminded.university.api.lesson;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.api.lesson.dto.LessonMaterialStatusResponse;
import ua.foxminded.university.api.lesson.dto.LessonMaterialUploadIntentRequest;
import ua.foxminded.university.api.lesson.dto.LessonMaterialUploadIntentResponse;
import ua.foxminded.university.services.LessonMaterialService;
import ua.foxminded.university.services.LessonMaterialStatusDetails;
import ua.foxminded.university.services.LessonMaterialUploadIntent;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class LessonMaterialRestController {

    private final LessonMaterialService lessonMaterialService;

    @GetMapping("/{lessonId}/materials/{materialId}")
    public LessonMaterialStatusResponse getMaterialStatus(
            @PathVariable int lessonId,
            @PathVariable int materialId,
            Authentication authentication
    ) {
        LessonMaterialStatusDetails details =
                lessonMaterialService.getMaterialStatus(
                        lessonId,
                        materialId,
                        authentication.getName()
                );

        return new LessonMaterialStatusResponse(
                details.materialId(),
                details.lessonId(),
                details.originalFilename(),
                details.contentType(),
                details.status(),
                details.expectedSizeBytes(),
                details.actualSizeBytes(),
                details.createdAt(),
                details.uploadedAt(),
                details.processingStartedAt(),
                details.processedAt(),
                details.failureReason()
        );
    }

    @PostMapping("/{lessonId}/materials/upload-intent")
    @ResponseStatus(HttpStatus.CREATED)
    public LessonMaterialUploadIntentResponse createUploadIntent(
            @PathVariable int lessonId,
            @Valid @RequestBody LessonMaterialUploadIntentRequest request,
            Authentication authentication
    ) {
        LessonMaterialUploadIntent uploadIntent =
                lessonMaterialService.createUploadIntent(
                        lessonId,
                        authentication.getName(),
                        request.originalFilename(),
                        request.contentType(),
                        request.expectedSizeBytes()
                );

        return new LessonMaterialUploadIntentResponse(
                uploadIntent.materialId(),
                uploadIntent.uploadUrl().toString(),
                uploadIntent.uploadMethod(),
                uploadIntent.expiresAt(),
                uploadIntent.contentType(),
                uploadIntent.formFields()
        );
    }
}
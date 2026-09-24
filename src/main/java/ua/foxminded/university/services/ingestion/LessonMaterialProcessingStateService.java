package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.LessonMaterialEmbeddedChunk;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.services.ingestion.model.LessonMaterialProcessingTarget;

import java.util.List;
import java.util.Optional;

public interface LessonMaterialProcessingStateService {

    Optional<LessonMaterialProcessingTarget> startProcessing(
            LessonMaterialObjectCreatedEvent event
    );

    void completeProcessing(
            int materialId,
            List<LessonMaterialEmbeddedChunk> chunks
    );

    void failProcessing(
            int materialId,
            String failureReason
    );
}
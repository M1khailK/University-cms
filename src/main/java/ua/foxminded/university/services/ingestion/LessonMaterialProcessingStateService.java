package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.services.ingestion.model.LessonMaterialProcessingTarget;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.List;
import java.util.Optional;

public interface LessonMaterialProcessingStateService {

    Optional<LessonMaterialProcessingTarget> startProcessing(
            LessonMaterialObjectCreatedEvent event
    );

    void completeProcessing(
            int materialId,
            List<LessonMaterialTextChunk> chunks
    );

    void failProcessing(
            int materialId,
            String failureReason
    );
}
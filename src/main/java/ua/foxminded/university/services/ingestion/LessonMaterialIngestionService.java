package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;

public interface LessonMaterialIngestionService {

    void processObjectCreated(
            LessonMaterialObjectCreatedEvent event
    );
}
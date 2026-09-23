package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;

public interface LessonMaterialDocumentProcessor {

    void process(LessonMaterialObjectCreatedEvent event);
}
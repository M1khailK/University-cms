package ua.foxminded.university.services.ingestion;

public interface LessonMaterialIngestionService {

    void processObjectCreated(
            LessonMaterialObjectCreatedEvent event
    );
}
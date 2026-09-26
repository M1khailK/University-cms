package ua.foxminded.university.services.ingestion.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ua.foxminded.university.services.ingestion.LessonMaterialDocumentProcessor;
import ua.foxminded.university.services.ingestion.LessonMaterialIngestionService;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;

import java.util.List;

@Component
@ConditionalOnProperty(
        name = "spring.cloud.aws.sqs.enabled",
        havingValue = "true"
)
public class LessonMaterialSqsListener {

    private final LessonMaterialS3EventParser eventParser;
    private final LessonMaterialIngestionService ingestionService;
    private final LessonMaterialDocumentProcessor documentProcessor;

    public LessonMaterialSqsListener(
            LessonMaterialS3EventParser eventParser,
            LessonMaterialIngestionService ingestionService,
            LessonMaterialDocumentProcessor documentProcessor
    ) {
        this.eventParser = eventParser;
        this.ingestionService = ingestionService;
        this.documentProcessor = documentProcessor;
    }

    @SqsListener(
            value = "${app.ingestion.lesson-material.queue-name}",
            acknowledgementMode = "ON_SUCCESS"
    )
    public void onMessage(String body) {
        List<LessonMaterialObjectCreatedEvent> events =
                eventParser.parse(body);

        for (LessonMaterialObjectCreatedEvent event : events) {
            ingestionService.processObjectCreated(event);
            documentProcessor.process(event);
        }
    }
}
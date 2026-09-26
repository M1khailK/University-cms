package ua.foxminded.university.services.ingestion.sqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.services.ingestion.LessonMaterialDocumentProcessor;
import ua.foxminded.university.services.ingestion.LessonMaterialIngestionService;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialSqsListenerTest {

    private static final String MESSAGE_BODY = "S3 event JSON";

    @Mock
    private LessonMaterialS3EventParser eventParser;

    @Mock
    private LessonMaterialIngestionService ingestionService;

    @Mock
    private LessonMaterialDocumentProcessor documentProcessor;

    private LessonMaterialSqsListener listener;

    @BeforeEach
    void setUp() {
        listener = new LessonMaterialSqsListener(
                eventParser,
                ingestionService,
                documentProcessor
        );
    }

    @Test
    void onMessage_shouldValidateAndProcessEveryEvent() {
        LessonMaterialObjectCreatedEvent first =
                new LessonMaterialObjectCreatedEvent(
                        "lesson-materials/1/first",
                        100L,
                        "version-1",
                        "sequencer-1"
                );

        LessonMaterialObjectCreatedEvent second =
                new LessonMaterialObjectCreatedEvent(
                        "lesson-materials/2/second",
                        200L,
                        "version-2",
                        "sequencer-2"
                );

        when(eventParser.parse(MESSAGE_BODY))
                .thenReturn(List.of(first, second));

        listener.onMessage(MESSAGE_BODY);

        InOrder order = inOrder(
                eventParser,
                ingestionService,
                documentProcessor
        );

        order.verify(eventParser).parse(MESSAGE_BODY);

        order.verify(ingestionService)
                .processObjectCreated(first);

        order.verify(documentProcessor).process(first);

        order.verify(ingestionService)
                .processObjectCreated(second);

        order.verify(documentProcessor).process(second);
    }

    @Test
    void onMessage_shouldDoNothing_whenParserReturnsNoEvents() {
        when(eventParser.parse(MESSAGE_BODY))
                .thenReturn(List.of());

        listener.onMessage(MESSAGE_BODY);

        verify(eventParser).parse(MESSAGE_BODY);

        verifyNoInteractions(
                ingestionService,
                documentProcessor
        );
    }
}
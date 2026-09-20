package ua.foxminded.university.services.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LessonMaterialS3EventParserTest {

    private final LessonMaterialS3EventParser parser =
            new LessonMaterialS3EventParser(new ObjectMapper());

    @Test
    void parse_shouldReturnObjectCreatedEvent() {
        String body = """
                {
                  "Records": [
                    {
                      "eventName": "ObjectCreated:Put",
                      "s3": {
                        "object": {
                          "key": "lesson-materials%2F1%2Fmaterial.pdf",
                          "size": 164293,
                          "versionId": "version-123",
                          "sequencer": "sequencer-123"
                        }
                      }
                    }
                  ]
                }
                """;

        List<LessonMaterialObjectCreatedEvent> events =
                parser.parse(body);

        assertEquals(1, events.size());

        LessonMaterialObjectCreatedEvent event =
                events.getFirst();

        assertEquals(
                "lesson-materials/1/material.pdf",
                event.objectKey()
        );
        assertEquals(164293L, event.sizeBytes());
        assertEquals("version-123", event.versionId());
        assertEquals("sequencer-123", event.sequencer());
    }

    @Test
    void parse_shouldReturnEmptyList_forS3TestEvent() {
        String body = """
                {
                  "Service": "Amazon S3",
                  "Event": "s3:TestEvent"
                }
                """;

        List<LessonMaterialObjectCreatedEvent> events =
                parser.parse(body);

        assertTrue(events.isEmpty());
    }

    @Test
    void parse_shouldThrow_whenRecordsAreMissing() {
        String body = """
                {
                  "unexpected": true
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(body)
        );
    }

    @Test
    void parse_shouldThrow_whenRequiredObjectFieldIsMissing() {
        String body = """
                {
                  "Records": [
                    {
                      "eventName": "ObjectCreated:Put",
                      "s3": {
                        "object": {
                          "key": "lesson-materials/1/material.pdf",
                          "size": 164293,
                          "sequencer": "sequencer-123"
                        }
                      }
                    }
                  ]
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(body)
        );
    }
}
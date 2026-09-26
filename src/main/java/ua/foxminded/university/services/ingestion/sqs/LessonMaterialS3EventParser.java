package ua.foxminded.university.services.ingestion.sqs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class LessonMaterialS3EventParser {

    private final ObjectMapper objectMapper;

    public LessonMaterialS3EventParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<LessonMaterialObjectCreatedEvent> parse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);

            if (isS3TestEvent(root)) {
                return List.of();
            }

            JsonNode records = root.get("Records");

            if (records == null || !records.isArray()) {
                throw new IllegalArgumentException(
                        "S3 event does not contain Records."
                );
            }

            List<LessonMaterialObjectCreatedEvent> events =
                    new ArrayList<>();

            for (JsonNode record : records) {
                String eventName = requiredText(
                        record,
                        "eventName"
                );

                if (!eventName.startsWith("ObjectCreated:")) {
                    continue;
                }

                JsonNode object = record
                        .path("s3")
                        .path("object");

                String encodedObjectKey = requiredText(
                        object,
                        "key"
                );

                String objectKey = URLDecoder.decode(
                        encodedObjectKey,
                        StandardCharsets.UTF_8
                );

                long sizeBytes = requiredLong(
                        object,
                        "size"
                );

                String versionId = requiredText(
                        object,
                        "versionId"
                );

                String sequencer = requiredText(
                        object,
                        "sequencer"
                );

                events.add(
                        new LessonMaterialObjectCreatedEvent(
                                objectKey,
                                sizeBytes,
                                versionId,
                                sequencer
                        )
                );
            }

            return List.copyOf(events);

        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Failed to parse S3 event.",
                    exception
            );
        }
    }

    private boolean isS3TestEvent(JsonNode root) {
        return "s3:TestEvent".equals(
                root.path("Event").asText()
        );
    }

    private String requiredText(
            JsonNode node,
            String fieldName
    ) {
        JsonNode field = node.get(fieldName);

        if (field == null || !field.isTextual() || field.asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required S3 event field: " + fieldName
            );
        }

        return field.asText();
    }

    private long requiredLong(
            JsonNode node,
            String fieldName
    ) {
        JsonNode field = node.get(fieldName);

        if (field == null || !field.canConvertToLong()) {
            throw new IllegalArgumentException(
                    "Missing required S3 event field: " + fieldName
            );
        }

        return field.asLong();
    }
}
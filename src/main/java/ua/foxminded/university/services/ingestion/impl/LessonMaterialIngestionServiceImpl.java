package ua.foxminded.university.services.ingestion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.ingestion.LessonMaterialIngestionService;
import ua.foxminded.university.services.ingestion.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.storage.ObjectMetadataReader;
import ua.foxminded.university.storage.StoredObjectMetadata;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LessonMaterialIngestionServiceImpl
        implements LessonMaterialIngestionService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final LessonMaterialRepository lessonMaterialRepository;
    private final ObjectMetadataReader objectMetadataReader;
    private final Clock clock;

    @Override
    @Transactional
    public void processObjectCreated(
            LessonMaterialObjectCreatedEvent event
    ) {
        LessonMaterial material = lessonMaterialRepository
                .findByObjectKey(event.objectKey())
                .orElseThrow(() -> new IllegalStateException(
                        "Lesson material not found for object key: "
                                + event.objectKey()
                ));

        if (isAlreadyProcessed(material, event)) {
            return;
        }

        if (material.getStatus() != LessonMaterialStatus.PENDING_UPLOAD) {
            throw new IllegalStateException(
                    "Unexpected lesson material status: "
                            + material.getStatus()
            );
        }

        StoredObjectMetadata metadata = objectMetadataReader.read(
                event.objectKey(),
                event.versionId()
        );

        if (!isValid(material, event, metadata)) {
            markFailed(material, event, metadata);
            return;
        }

        material.setActualSizeBytes(metadata.sizeBytes());
        material.setS3VersionId(event.versionId());
        material.setS3Sequencer(event.sequencer());
        material.setUploadedAt(Instant.now(clock));
        material.setStatus(LessonMaterialStatus.UPLOADED);
        material.setFailureReason(null);
    }

    private boolean isAlreadyProcessed(
            LessonMaterial material,
            LessonMaterialObjectCreatedEvent event
    ) {
        return material.getStatus() != LessonMaterialStatus.PENDING_UPLOAD
                && Objects.equals(
                material.getS3VersionId(),
                event.versionId()
        );
    }

    private boolean isValid(
            LessonMaterial material,
            LessonMaterialObjectCreatedEvent event,
            StoredObjectMetadata metadata
    ) {
        return metadata.sizeBytes() == event.sizeBytes()
                && metadata.sizeBytes() == material.getExpectedSizeBytes()
                && PDF_CONTENT_TYPE.equalsIgnoreCase(metadata.contentType())
                && Objects.equals(
                metadata.versionId(),
                event.versionId()
        );
    }

    private void markFailed(
            LessonMaterial material,
            LessonMaterialObjectCreatedEvent event,
            StoredObjectMetadata metadata
    ) {
        material.setActualSizeBytes(metadata.sizeBytes());
        material.setS3VersionId(event.versionId());
        material.setS3Sequencer(event.sequencer());
        material.setStatus(LessonMaterialStatus.FAILED);
        material.setFailureReason(
                "Uploaded object metadata does not match upload intent."
        );
    }
}
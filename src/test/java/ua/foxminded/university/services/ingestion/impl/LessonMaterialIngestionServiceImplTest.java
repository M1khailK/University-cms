package ua.foxminded.university.services.ingestion.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.storage.ObjectMetadataReader;
import ua.foxminded.university.storage.StoredObjectMetadata;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialIngestionServiceImplTest {

    private static final String OBJECT_KEY =
            "lesson-materials/17/material-123";

    private static final String VERSION_ID = "version-123";

    private static final String SEQUENCER = "sequencer-123";

    private static final long EXPECTED_SIZE_BYTES = 1024L;

    private static final Instant NOW =
            Instant.parse("2026-09-23T12:00:00Z");

    @Mock
    private LessonMaterialRepository lessonMaterialRepository;

    @Mock
    private ObjectMetadataReader objectMetadataReader;

    private LessonMaterialIngestionServiceImpl ingestionService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

        ingestionService = new LessonMaterialIngestionServiceImpl(
                lessonMaterialRepository,
                objectMetadataReader,
                clock
        );
    }

    @Test
    void processObjectCreated_shouldMarkMaterialUploaded_whenMetadataMatches() {
        LessonMaterial material = pendingMaterial();
        LessonMaterialObjectCreatedEvent event = objectCreatedEvent();

        when(lessonMaterialRepository.findByObjectKeyForUpdate(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        when(objectMetadataReader.read(OBJECT_KEY, VERSION_ID))
                .thenReturn(new StoredObjectMetadata(
                        EXPECTED_SIZE_BYTES,
                        "application/pdf",
                        VERSION_ID
                ));

        ingestionService.processObjectCreated(event);

        assertEquals(
                LessonMaterialStatus.UPLOADED,
                material.getStatus()
        );
        assertEquals(
                Long.valueOf(EXPECTED_SIZE_BYTES),
                material.getActualSizeBytes()
        );
        assertEquals(VERSION_ID, material.getS3VersionId());
        assertEquals(SEQUENCER, material.getS3Sequencer());
        assertEquals(NOW, material.getUploadedAt());
        assertNull(material.getFailureReason());

        verify(objectMetadataReader).read(
                OBJECT_KEY,
                VERSION_ID
        );
    }

    @Test
    void processObjectCreated_shouldIgnoreDuplicate_forSameVersion() {
        LessonMaterial material = pendingMaterial();
        Instant originalUploadedAt =
                Instant.parse("2026-09-22T12:00:00Z");

        material.setStatus(LessonMaterialStatus.UPLOADED);
        material.setS3VersionId(VERSION_ID);
        material.setS3Sequencer(SEQUENCER);
        material.setActualSizeBytes(EXPECTED_SIZE_BYTES);
        material.setUploadedAt(originalUploadedAt);

        when(lessonMaterialRepository.findByObjectKeyForUpdate(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        ingestionService.processObjectCreated(objectCreatedEvent());

        assertEquals(
                LessonMaterialStatus.UPLOADED,
                material.getStatus()
        );
        assertEquals(originalUploadedAt, material.getUploadedAt());
        assertEquals(VERSION_ID, material.getS3VersionId());

        verifyNoInteractions(objectMetadataReader);
    }

    @Test
    void processObjectCreated_shouldMarkMaterialFailed_whenSizeDoesNotMatch() {
        LessonMaterial material = pendingMaterial();

        when(lessonMaterialRepository.findByObjectKeyForUpdate(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        when(objectMetadataReader.read(OBJECT_KEY, VERSION_ID))
                .thenReturn(new StoredObjectMetadata(
                        512L,
                        "application/pdf",
                        VERSION_ID
                ));

        ingestionService.processObjectCreated(objectCreatedEvent());

        assertEquals(
                LessonMaterialStatus.FAILED,
                material.getStatus()
        );
        assertEquals(Long.valueOf(512L), material.getActualSizeBytes());
        assertEquals(VERSION_ID, material.getS3VersionId());
        assertEquals(SEQUENCER, material.getS3Sequencer());
        assertNull(material.getUploadedAt());
        assertNotNull(material.getFailureReason());
        assertFalse(material.getFailureReason().isBlank());
    }

    @Test
    void processObjectCreated_shouldMarkMaterialFailed_whenVersionDoesNotMatch() {
        LessonMaterial material = pendingMaterial();

        when(lessonMaterialRepository.findByObjectKeyForUpdate(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        when(objectMetadataReader.read(OBJECT_KEY, VERSION_ID))
                .thenReturn(new StoredObjectMetadata(
                        EXPECTED_SIZE_BYTES,
                        "application/pdf",
                        "another-version"
                ));

        ingestionService.processObjectCreated(objectCreatedEvent());

        assertEquals(
                LessonMaterialStatus.FAILED,
                material.getStatus()
        );
        assertNull(material.getUploadedAt());
        assertNotNull(material.getFailureReason());
    }

    @Test
    void processObjectCreated_shouldPropagateTemporaryStorageFailure() {
        LessonMaterial material = pendingMaterial();
        IllegalStateException storageFailure =
                new IllegalStateException("S3 temporarily unavailable");

        when(lessonMaterialRepository.findByObjectKeyForUpdate(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        when(objectMetadataReader.read(OBJECT_KEY, VERSION_ID))
                .thenThrow(storageFailure);

        IllegalStateException actualException = assertThrows(
                IllegalStateException.class,
                () -> ingestionService.processObjectCreated(
                        objectCreatedEvent()
                )
        );

        assertSame(storageFailure, actualException);
        assertEquals(
                LessonMaterialStatus.PENDING_UPLOAD,
                material.getStatus()
        );
        assertNull(material.getUploadedAt());
    }

    private LessonMaterial pendingMaterial() {
        LessonMaterial material = new LessonMaterial();
        material.setObjectKey(OBJECT_KEY);
        material.setExpectedSizeBytes(EXPECTED_SIZE_BYTES);
        material.setStatus(LessonMaterialStatus.PENDING_UPLOAD);
        return material;
    }

    private LessonMaterialObjectCreatedEvent objectCreatedEvent() {
        return new LessonMaterialObjectCreatedEvent(
                OBJECT_KEY,
                EXPECTED_SIZE_BYTES,
                VERSION_ID,
                SEQUENCER
        );
    }
}
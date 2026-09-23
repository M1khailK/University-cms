package ua.foxminded.university.services.ingestion.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialChunk;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.repository.LessonMaterialChunkRepository;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.services.ingestion.model.LessonMaterialProcessingTarget;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialProcessingStateServiceImplTest {

    private static final int MATERIAL_ID = 10;

    private static final String OBJECT_KEY =
            "lesson-materials/1/document";

    private static final String VERSION_ID = "version-10";

    private static final Instant NOW =
            Instant.parse("2026-09-23T14:00:00Z");

    @Mock
    private LessonMaterialRepository lessonMaterialRepository;

    @Mock
    private LessonMaterialChunkRepository chunkRepository;

    @Captor
    private ArgumentCaptor<Iterable<LessonMaterialChunk>>
            savedChunksCaptor;

    private LessonMaterialProcessingStateServiceImpl stateService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

        stateService = new LessonMaterialProcessingStateServiceImpl(
                lessonMaterialRepository,
                chunkRepository,
                clock
        );
    }

    @Test
    void startProcessing_shouldMoveUploadedMaterialToProcessing() {
        LessonMaterial material =
                materialWithStatus(LessonMaterialStatus.UPLOADED);

        when(lessonMaterialRepository.findByObjectKey(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        Optional<LessonMaterialProcessingTarget> actual =
                stateService.startProcessing(objectCreatedEvent());

        assertTrue(actual.isPresent());
        assertEquals(
                new LessonMaterialProcessingTarget(
                        MATERIAL_ID,
                        OBJECT_KEY,
                        VERSION_ID
                ),
                actual.orElseThrow()
        );
        assertEquals(
                LessonMaterialStatus.PROCESSING,
                material.getStatus()
        );
        assertNull(material.getProcessedAt());
        assertNull(material.getFailureReason());
    }

    @Test
    void startProcessing_shouldReturnEmpty_whenMaterialIsReady() {
        LessonMaterial material =
                materialWithStatus(LessonMaterialStatus.READY);

        material.setProcessedAt(NOW);

        when(lessonMaterialRepository.findByObjectKey(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        Optional<LessonMaterialProcessingTarget> actual =
                stateService.startProcessing(objectCreatedEvent());

        assertTrue(actual.isEmpty());
        assertEquals(
                LessonMaterialStatus.READY,
                material.getStatus()
        );
        assertEquals(NOW, material.getProcessedAt());

        verifyNoInteractions(chunkRepository);
    }

    @Test
    void startProcessing_shouldRejectDifferentObjectVersion() {
        LessonMaterial material =
                materialWithStatus(LessonMaterialStatus.UPLOADED);

        material.setS3VersionId("another-version");

        when(lessonMaterialRepository.findByObjectKey(OBJECT_KEY))
                .thenReturn(Optional.of(material));

        assertThrows(
                IllegalStateException.class,
                () -> stateService.startProcessing(
                        objectCreatedEvent()
                )
        );

        assertEquals(
                LessonMaterialStatus.UPLOADED,
                material.getStatus()
        );

        verifyNoInteractions(chunkRepository);
    }

    @Test
    void completeProcessing_shouldReplaceChunksAndMarkReady() {
        LessonMaterial material =
                materialWithStatus(LessonMaterialStatus.PROCESSING);

        when(lessonMaterialRepository.findByIdForUpdate(MATERIAL_ID))
                .thenReturn(Optional.of(material));

        List<LessonMaterialTextChunk> chunks = List.of(
                new LessonMaterialTextChunk(
                        MATERIAL_ID,
                        1,
                        0,
                        "First chunk"
                ),
                new LessonMaterialTextChunk(
                        MATERIAL_ID,
                        2,
                        0,
                        "Second chunk"
                )
        );

        stateService.completeProcessing(MATERIAL_ID, chunks);

        assertEquals(
                LessonMaterialStatus.READY,
                material.getStatus()
        );
        assertEquals(NOW, material.getProcessedAt());
        assertNull(material.getFailureReason());

        InOrder order = inOrder(chunkRepository);

        order.verify(chunkRepository)
                .deleteAllByMaterialId(MATERIAL_ID);

        order.verify(chunkRepository).flush();

        order.verify(chunkRepository)
                .saveAll(savedChunksCaptor.capture());

        List<LessonMaterialChunk> savedChunks =
                StreamSupport.stream(
                        savedChunksCaptor.getValue().spliterator(),
                        false
                ).toList();

        assertEquals(2, savedChunks.size());

        assertSame(material, savedChunks.get(0).getMaterial());
        assertEquals(1, savedChunks.get(0).getPageNumber());
        assertEquals(0, savedChunks.get(0).getChunkIndex());
        assertEquals("First chunk", savedChunks.get(0).getText());

        assertSame(material, savedChunks.get(1).getMaterial());
        assertEquals(2, savedChunks.get(1).getPageNumber());
        assertEquals(0, savedChunks.get(1).getChunkIndex());
        assertEquals("Second chunk", savedChunks.get(1).getText());
    }

    @Test
    void failProcessing_shouldRemoveChunksAndMarkFailed() {
        LessonMaterial material =
                materialWithStatus(LessonMaterialStatus.PROCESSING);

        when(lessonMaterialRepository.findByIdForUpdate(MATERIAL_ID))
                .thenReturn(Optional.of(material));

        stateService.failProcessing(
                MATERIAL_ID,
                "PDF contains no extractable text."
        );

        assertEquals(
                LessonMaterialStatus.FAILED,
                material.getStatus()
        );
        assertNull(material.getProcessedAt());
        assertEquals(
                "PDF contains no extractable text.",
                material.getFailureReason()
        );

        InOrder order = inOrder(chunkRepository);

        order.verify(chunkRepository)
                .deleteAllByMaterialId(MATERIAL_ID);

        order.verify(chunkRepository).flush();
    }

    private LessonMaterial materialWithStatus(
            LessonMaterialStatus status
    ) {
        LessonMaterial material = new LessonMaterial();
        material.setId(MATERIAL_ID);
        material.setObjectKey(OBJECT_KEY);
        material.setS3VersionId(VERSION_ID);
        material.setStatus(status);
        return material;
    }

    private LessonMaterialObjectCreatedEvent objectCreatedEvent() {
        return new LessonMaterialObjectCreatedEvent(
                OBJECT_KEY,
                1024L,
                VERSION_ID,
                "sequencer-10"
        );
    }
}
package ua.foxminded.university.services.ingestion.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.customexceptions.InvalidPdfContentException;
import ua.foxminded.university.customexceptions.StorageUnavailableException;
import ua.foxminded.university.services.ingestion.LessonMaterialProcessingStateService;
import ua.foxminded.university.services.ingestion.LessonMaterialTextChunker;
import ua.foxminded.university.services.ingestion.PdfTextExtractor;
import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.services.ingestion.model.LessonMaterialProcessingTarget;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;
import ua.foxminded.university.storage.ObjectContentReader;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialDocumentProcessorImplTest {

    private static final int MATERIAL_ID = 10;

    private static final String OBJECT_KEY =
            "lesson-materials/1/document";

    private static final String VERSION_ID = "version-10";

    private static final LessonMaterialObjectCreatedEvent EVENT =
            new LessonMaterialObjectCreatedEvent(
                    OBJECT_KEY,
                    1024L,
                    VERSION_ID,
                    "sequencer-10"
            );

    private static final LessonMaterialProcessingTarget TARGET =
            new LessonMaterialProcessingTarget(
                    MATERIAL_ID,
                    OBJECT_KEY,
                    VERSION_ID
            );

    private static final byte[] PDF_BYTES =
            new byte[]{1, 2, 3};

    @Mock
    private LessonMaterialProcessingStateService stateService;

    @Mock
    private ObjectContentReader objectContentReader;

    @Mock
    private PdfTextExtractor pdfTextExtractor;

    @Mock
    private LessonMaterialTextChunker textChunker;

    private LessonMaterialDocumentProcessorImpl processor;

    @BeforeEach
    void setUp() {
        processor = new LessonMaterialDocumentProcessorImpl(
                stateService,
                objectContentReader,
                pdfTextExtractor,
                textChunker
        );
    }

    @Test
    void process_shouldReadExtractChunkAndComplete() {
        List<ExtractedPdfPage> pages = List.of(
                new ExtractedPdfPage(
                        1,
                        "Transactions and persistence"
                )
        );

        List<LessonMaterialTextChunk> chunks = List.of(
                new LessonMaterialTextChunk(
                        MATERIAL_ID,
                        1,
                        0,
                        "Transactions and persistence"
                )
        );

        when(stateService.startProcessing(EVENT))
                .thenReturn(Optional.of(TARGET));

        when(objectContentReader.read(OBJECT_KEY, VERSION_ID))
                .thenReturn(PDF_BYTES);

        when(pdfTextExtractor.extract(PDF_BYTES))
                .thenReturn(pages);

        when(textChunker.chunk(MATERIAL_ID, pages))
                .thenReturn(chunks);

        processor.process(EVENT);

        InOrder order = inOrder(
                stateService,
                objectContentReader,
                pdfTextExtractor,
                textChunker
        );

        order.verify(stateService).startProcessing(EVENT);

        order.verify(objectContentReader)
                .read(OBJECT_KEY, VERSION_ID);

        order.verify(pdfTextExtractor).extract(PDF_BYTES);

        order.verify(textChunker)
                .chunk(MATERIAL_ID, pages);

        order.verify(stateService)
                .completeProcessing(MATERIAL_ID, chunks);
    }

    @Test
    void process_shouldDoNothing_whenMaterialIsTerminal() {
        when(stateService.startProcessing(EVENT))
                .thenReturn(Optional.empty());

        processor.process(EVENT);

        verify(stateService).startProcessing(EVENT);
        verifyNoMoreInteractions(stateService);

        verifyNoInteractions(
                objectContentReader,
                pdfTextExtractor,
                textChunker
        );
    }

    @Test
    void process_shouldMarkFailed_whenPdfContentIsInvalid() {
        InvalidPdfContentException failure =
                new InvalidPdfContentException(
                        "PDF contains no extractable text."
                );

        when(stateService.startProcessing(EVENT))
                .thenReturn(Optional.of(TARGET));

        when(objectContentReader.read(OBJECT_KEY, VERSION_ID))
                .thenReturn(PDF_BYTES);

        when(pdfTextExtractor.extract(PDF_BYTES))
                .thenThrow(failure);

        processor.process(EVENT);

        verify(stateService).failProcessing(
                MATERIAL_ID,
                "PDF contains no extractable text."
        );

        verify(stateService, never())
                .completeProcessing(anyInt(), anyList());

        verifyNoInteractions(textChunker);
    }

    @Test
    void process_shouldPropagateTemporaryStorageFailure() {
        StorageUnavailableException failure =
                new StorageUnavailableException(
                        "S3 temporarily unavailable."
                );

        when(stateService.startProcessing(EVENT))
                .thenReturn(Optional.of(TARGET));

        when(objectContentReader.read(OBJECT_KEY, VERSION_ID))
                .thenThrow(failure);

        StorageUnavailableException actual = assertThrows(
                StorageUnavailableException.class,
                () -> processor.process(EVENT)
        );

        assertSame(failure, actual);

        verify(stateService, never())
                .completeProcessing(anyInt(), anyList());

        verify(stateService, never())
                .failProcessing(anyInt(), anyString());

        verifyNoInteractions(
                pdfTextExtractor,
                textChunker
        );
    }
}
package ua.foxminded.university.services.ingestion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.InvalidPdfContentException;
import ua.foxminded.university.services.ingestion.LessonMaterialDocumentProcessor;
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

@Service
@RequiredArgsConstructor
public class LessonMaterialDocumentProcessorImpl
        implements LessonMaterialDocumentProcessor {

    private static final String DEFAULT_INVALID_PDF_REASON =
            "PDF content cannot be processed.";

    private final LessonMaterialProcessingStateService stateService;
    private final ObjectContentReader objectContentReader;
    private final PdfTextExtractor pdfTextExtractor;
    private final LessonMaterialTextChunker textChunker;

    @Override
    public void process(LessonMaterialObjectCreatedEvent event) {
        Optional<LessonMaterialProcessingTarget> targetOptional =
                stateService.startProcessing(event);

        if (targetOptional.isEmpty()) {
            return;
        }

        LessonMaterialProcessingTarget target =
                targetOptional.orElseThrow();

        try {
            byte[] pdfBytes = objectContentReader.read(
                    target.objectKey(),
                    target.versionId()
            );

            List<ExtractedPdfPage> pages =
                    pdfTextExtractor.extract(pdfBytes);

            List<LessonMaterialTextChunk> chunks =
                    textChunker.chunk(
                            target.materialId(),
                            pages
                    );

            stateService.completeProcessing(
                    target.materialId(),
                    chunks
            );
        } catch (InvalidPdfContentException exception) {
            stateService.failProcessing(
                    target.materialId(),
                    failureReason(exception)
            );
        }
    }

    private String failureReason(
            InvalidPdfContentException exception
    ) {
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            return DEFAULT_INVALID_PDF_REASON;
        }

        return message;
    }
}
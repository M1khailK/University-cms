package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.ExtractedPdfPage;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.List;

public interface LessonMaterialTextChunker {

    List<LessonMaterialTextChunk> chunk(
            int materialId,
            List<ExtractedPdfPage> pages
    );
}
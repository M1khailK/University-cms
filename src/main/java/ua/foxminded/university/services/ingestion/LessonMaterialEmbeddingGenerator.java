package ua.foxminded.university.services.ingestion;

import ua.foxminded.university.services.ingestion.model.LessonMaterialEmbeddedChunk;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.util.List;

public interface LessonMaterialEmbeddingGenerator {

    List<LessonMaterialEmbeddedChunk> generate(
            List<LessonMaterialTextChunk> chunks
    );
}
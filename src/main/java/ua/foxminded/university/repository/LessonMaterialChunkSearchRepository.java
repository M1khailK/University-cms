package ua.foxminded.university.repository;

import ua.foxminded.university.repository.model
        .LessonMaterialChunkSearchResult;

import java.util.Collection;
import java.util.List;

public interface LessonMaterialChunkSearchRepository {

    List<LessonMaterialChunkSearchResult> findNearest(
            Collection<Integer> lessonIds,
            float[] queryEmbedding,
            int limit
    );
}
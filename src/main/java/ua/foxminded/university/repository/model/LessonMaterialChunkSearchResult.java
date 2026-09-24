package ua.foxminded.university.repository.model;

public record LessonMaterialChunkSearchResult(
        long chunkId,
        int materialId,
        int lessonId,
        String originalFilename,
        int pageNumber,
        int chunkIndex,
        String text,
        double distance
) {
}
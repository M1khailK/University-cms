package ua.foxminded.university.services.search.model;

public record LessonMaterialSearchResult(
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
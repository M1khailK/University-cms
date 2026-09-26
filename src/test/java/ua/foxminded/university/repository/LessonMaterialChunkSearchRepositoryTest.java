package ua.foxminded.university.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc
        .AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialChunk;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.repository.impl
        .LessonMaterialChunkSearchRepositoryImpl;
import ua.foxminded.university.repository.model
        .LessonMaterialChunkSearchResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.datasource.url="
                + "jdbc:tc:pgvector:0.8.6-pg17"
                + ":///lesson-material-search-test",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(LessonMaterialChunkSearchRepositoryImpl.class)
class LessonMaterialChunkSearchRepositoryTest {

    private static final int EMBEDDING_DIMENSIONS = 768;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMaterialRepository materialRepository;

    @Autowired
    private LessonMaterialChunkRepository chunkRepository;

    @Autowired
    private LessonMaterialChunkSearchRepository searchRepository;

    @Test
    void findNearest_shouldReturnNearestReadyChunksOnlyFromAllowedLessons() {
        Lesson allowedLesson = createLesson(
                "Allowed lesson",
                LocalDate.of(2026, 9, 24)
        );

        Lesson forbiddenLesson = createLesson(
                "Forbidden lesson",
                LocalDate.of(2026, 9, 25)
        );

        LessonMaterial readyAllowedMaterial = createMaterial(
                allowedLesson,
                "allowed-ready.pdf",
                "allowed-ready",
                LessonMaterialStatus.READY
        );

        LessonMaterial uploadedAllowedMaterial = createMaterial(
                allowedLesson,
                "allowed-uploaded.pdf",
                "allowed-uploaded",
                LessonMaterialStatus.UPLOADED
        );

        LessonMaterial readyForbiddenMaterial = createMaterial(
                forbiddenLesson,
                "forbidden-ready.pdf",
                "forbidden-ready",
                LessonMaterialStatus.READY
        );

        chunkRepository.saveAllAndFlush(List.of(
                createChunk(
                        readyAllowedMaterial,
                        1,
                        0,
                        "Closest allowed chunk",
                        vector(0.9f, 0.1f)
                ),
                createChunk(
                        readyAllowedMaterial,
                        1,
                        1,
                        "Second allowed chunk",
                        vector(0.6f, 0.4f)
                ),
                createChunk(
                        readyAllowedMaterial,
                        2,
                        0,
                        "Third allowed chunk",
                        vector(0.0f, 1.0f)
                ),
                createChunk(
                        uploadedAllowedMaterial,
                        1,
                        0,
                        "Not ready chunk",
                        vector(1.0f, 0.0f)
                ),
                createChunk(
                        readyForbiddenMaterial,
                        1,
                        0,
                        "Forbidden lesson chunk",
                        vector(1.0f, 0.0f)
                )
        ));

        List<LessonMaterialChunkSearchResult> actual =
                searchRepository.findNearest(
                        List.of(allowedLesson.getId()),
                        vector(1.0f, 0.0f),
                        0.30,
                        3
                );

        assertEquals(2, actual.size());

        assertEquals(
                List.of(
                        "Closest allowed chunk",
                        "Second allowed chunk"
                ),
                actual.stream()
                        .map(LessonMaterialChunkSearchResult::text)
                        .toList()
        );

        assertTrue(
                actual.get(0).distance()
                        < actual.get(1).distance()
        );

        for (LessonMaterialChunkSearchResult result : actual) {
            assertEquals(
                    allowedLesson.getId(),
                    result.lessonId()
            );

            assertEquals(
                    readyAllowedMaterial.getId(),
                    result.materialId()
            );

            assertEquals(
                    "allowed-ready.pdf",
                    result.originalFilename()
            );
        }
    }

    private Lesson createLesson(
            String name,
            LocalDate date
    ) {
        Lesson lesson = new Lesson();
        lesson.setName(name);
        lesson.setDate(date);
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(11, 0));

        return lessonRepository.saveAndFlush(lesson);
    }

    private LessonMaterial createMaterial(
            Lesson lesson,
            String filename,
            String objectKeySuffix,
            LessonMaterialStatus status
    ) {
        Instant now = Instant.parse(
                "2026-09-24T10:00:00Z"
        );

        LessonMaterial material = new LessonMaterial();
        material.setLesson(lesson);
        material.setObjectKey(
                "lesson-materials/"
                        + lesson.getId()
                        + "/"
                        + objectKeySuffix
        );
        material.setOriginalFilename(filename);
        material.setContentType("application/pdf");
        material.setStatus(status);
        material.setExpectedSizeBytes(100L);
        material.setActualSizeBytes(100L);
        material.setCreatedAt(now);
        material.setUploadedAt(now);

        if (status == LessonMaterialStatus.READY) {
            material.setProcessedAt(now);
        }

        return materialRepository.saveAndFlush(material);
    }

    private LessonMaterialChunk createChunk(
            LessonMaterial material,
            int pageNumber,
            int chunkIndex,
            String text,
            float[] embedding
    ) {
        LessonMaterialChunk chunk =
                new LessonMaterialChunk();

        chunk.setMaterial(material);
        chunk.setPageNumber(pageNumber);
        chunk.setChunkIndex(chunkIndex);
        chunk.setText(text);
        chunk.setEmbedding(embedding);

        return chunk;
    }

    private float[] vector(
            float firstDimension,
            float secondDimension
    ) {
        float[] vector =
                new float[EMBEDDING_DIMENSIONS];

        vector[0] = firstDimension;
        vector[1] = secondDimension;

        return vector;
    }
}
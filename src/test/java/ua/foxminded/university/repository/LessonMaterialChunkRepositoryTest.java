package ua.foxminded.university.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialChunk;
import ua.foxminded.university.info.LessonMaterialStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(properties = {
        "spring.datasource.url="
                + "jdbc:tc:postgresql:15:///lesson-material-chunks-test",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class LessonMaterialChunkRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMaterialRepository materialRepository;

    @Autowired
    private LessonMaterialChunkRepository chunkRepository;

    @Test
    void findAllByMaterialId_shouldReturnChunksInPageOrder() {
        Lesson lesson = new Lesson();
        lesson.setName("Chunk repository test");
        lesson.setDate(LocalDate.of(2026, 9, 23));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(11, 0));
        lesson = lessonRepository.saveAndFlush(lesson);

        Instant now = Instant.parse("2026-09-23T10:00:00Z");

        LessonMaterial material = new LessonMaterial();
        material.setLesson(lesson);
        material.setObjectKey(
                "lesson-materials/" + lesson.getId() + "/chunk-test"
        );
        material.setOriginalFilename("lesson.pdf");
        material.setContentType("application/pdf");
        material.setStatus(LessonMaterialStatus.UPLOADED);
        material.setExpectedSizeBytes(100L);
        material.setActualSizeBytes(100L);
        material.setCreatedAt(now);
        material.setUploadedAt(now);
        material = materialRepository.saveAndFlush(material);

        chunkRepository.saveAllAndFlush(List.of(
                createChunk(material, 3, 1, "Third page, second chunk"),
                createChunk(material, 1, 0, "First page"),
                createChunk(material, 3, 0, "Third page, first chunk")
        ));

        List<LessonMaterialChunk> actual =
                chunkRepository
                        .findAllByMaterialIdOrderByPageNumberAscChunkIndexAsc(
                                material.getId()
                        );

        assertEquals(3, actual.size());

        assertEquals(
                List.of("First page",
                        "Third page, first chunk",
                        "Third page, second chunk"),
                actual.stream()
                        .map(LessonMaterialChunk::getText)
                        .toList()
        );

        assertEquals(
                List.of(1, 3, 3),
                actual.stream()
                        .map(LessonMaterialChunk::getPageNumber)
                        .toList()
        );

        assertEquals(
                List.of(0, 0, 1),
                actual.stream()
                        .map(LessonMaterialChunk::getChunkIndex)
                        .toList()
        );

        for (LessonMaterialChunk chunk : actual) {
            assertNotNull(chunk.getId());
            assertEquals(
                    material.getId(),
                    chunk.getMaterial().getId()
            );
        }
    }

    private LessonMaterialChunk createChunk(
            LessonMaterial material,
            int pageNumber,
            int chunkIndex,
            String text
    ) {
        LessonMaterialChunk chunk = new LessonMaterialChunk();
        chunk.setMaterial(material);
        chunk.setPageNumber(pageNumber);
        chunk.setChunkIndex(chunkIndex);
        chunk.setText(text);
        return chunk;
    }
}
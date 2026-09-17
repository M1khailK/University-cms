package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.config.repository.RepositoriesTestConfig;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.LessonMaterial;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@DataJpaTest
@Sql(scripts = {"/test_data.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = RepositoriesTestConfig.class)
public class LessonMaterialRepositoryTest {

    @Autowired
    private LessonMaterialRepository lessonMaterialRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Test
    public void findAllByLessonIdOrderByUploadedAtDesc_shouldReturnMaterialsNewestFirst() {
        Lesson lesson = createLesson();
        LessonMaterial olderMaterial = createMaterial(
                lesson,
                "lesson-materials/" + lesson.getId() + "/older",
                "lecture.pdf",
                Instant.parse("2026-09-17T10:00:00Z")
        );

        LessonMaterial newerMaterial = createMaterial(
                lesson,
                "lesson-materials/" + lesson.getId() + "/newer",
                "homework.pdf",
                Instant.parse("2026-09-17T12:00:00Z")
        );

        lessonMaterialRepository.saveAll(List.of(olderMaterial, newerMaterial));

        List<LessonMaterial> actual =
                lessonMaterialRepository.findAllByLessonIdOrderByUploadedAtDesc(
                        lesson.getId()
                );

        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("homework.pdf", actual.get(0).getOriginalFilename());
        Assertions.assertEquals("lecture.pdf", actual.get(1).getOriginalFilename());
    }

    private Lesson createLesson() {
        Lesson lesson = new Lesson();
        lesson.setName("Repository test lesson");
        lesson.setDate(LocalDate.of(2026, 9, 17));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(11, 30));

        return lessonRepository.saveAndFlush(lesson);
    }

    private LessonMaterial createMaterial(
            Lesson lesson,
            String objectKey,
            String originalFilename,
            Instant uploadedAt
    ) {
        LessonMaterial material = new LessonMaterial();
        material.setLesson(lesson);
        material.setObjectKey(objectKey);
        material.setOriginalFilename(originalFilename);
        material.setContentType("application/pdf");
        material.setSizeBytes(1024L);
        material.setUploadedAt(uploadedAt);

        return material;
    }
}
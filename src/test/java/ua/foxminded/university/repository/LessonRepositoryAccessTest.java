package ua.foxminded.university.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc
        .AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.datasource.url="
                + "jdbc:tc:pgvector:0.8.6-pg17"
                + ":///lesson-access-test",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class LessonRepositoryAccessTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Test
    void accessQueries_shouldReturnLessonsAllowedByUserRole() {
        List<Integer> studentLessonIds =
                lessonRepository.findIdsAccessibleToStudent(
                        "alice.fourth@example.com"
                );

        List<Integer> teacherLessonIds =
                lessonRepository.findIdsAccessibleToTeacher(
                        "bob.first@example.com"
                );

        List<Integer> allLessonIds =
                lessonRepository.findAllIds();

        assertEquals(List.of(1), studentLessonIds);
        assertEquals(List.of(1), teacherLessonIds);

        assertTrue(
                allLessonIds.containsAll(studentLessonIds)
        );

        assertTrue(
                allLessonIds.containsAll(teacherLessonIds)
        );

        assertTrue(allLessonIds.size() > 1);
    }

    @Test
    void accessQueries_shouldReturnEmptyListsForUnknownUsers() {
        assertTrue(
                lessonRepository
                        .findIdsAccessibleToStudent(
                                "unknown-student@example.com"
                        )
                        .isEmpty()
        );

        assertTrue(
                lessonRepository
                        .findIdsAccessibleToTeacher(
                                "unknown-teacher@example.com"
                        )
                        .isEmpty()
        );
    }
}
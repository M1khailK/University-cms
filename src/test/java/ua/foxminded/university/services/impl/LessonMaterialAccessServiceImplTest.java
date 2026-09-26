package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ua.foxminded.university.repository.LessonRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMaterialAccessServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    private LessonMaterialAccessServiceImpl accessService;

    @BeforeEach
    void setUp() {
        accessService =
                new LessonMaterialAccessServiceImpl(
                        lessonRepository
                );
    }

    @Test
    void findAccessibleLessonIds_shouldReturnStudentGroupLessons() {
        when(lessonRepository.findIdsAccessibleToStudent(
                "student@university.com"
        )).thenReturn(List.of(10, 20));

        List<Integer> actual =
                accessService.findAccessibleLessonIds(
                        " student@university.com ",
                        "ROLE_STUDENT"
                );

        assertEquals(List.of(10, 20), actual);

        verify(lessonRepository)
                .findIdsAccessibleToStudent(
                        "student@university.com"
                );
    }

    @Test
    void findAccessibleLessonIds_shouldReturnTeacherLessons() {
        when(lessonRepository.findIdsAccessibleToTeacher(
                "teacher@university.com"
        )).thenReturn(List.of(30, 40));

        List<Integer> actual =
                accessService.findAccessibleLessonIds(
                        "teacher@university.com",
                        "ROLE_TEACHER"
                );

        assertEquals(List.of(30, 40), actual);

        verify(lessonRepository)
                .findIdsAccessibleToTeacher(
                        "teacher@university.com"
                );
    }

    @Test
    void findAccessibleLessonIds_shouldReturnAllLessonsForAdmin() {
        when(lessonRepository.findAllIds())
                .thenReturn(List.of(10, 20, 30));

        List<Integer> actual =
                accessService.findAccessibleLessonIds(
                        "admin@university.com",
                        "ROLE_ADMIN"
                );

        assertEquals(List.of(10, 20, 30), actual);

        verify(lessonRepository).findAllIds();
    }

    @Test
    void findAccessibleLessonIds_shouldRejectUnsupportedRole() {
        assertThrows(
                AccessDeniedException.class,
                () -> accessService.findAccessibleLessonIds(
                        "user@university.com",
                        "ROLE_GUEST"
                )
        );

        verifyNoInteractions(lessonRepository);
    }

    @Test
    void findAccessibleLessonIds_shouldRejectBlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> accessService.findAccessibleLessonIds(
                        " ",
                        "ROLE_STUDENT"
                )
        );

        verifyNoInteractions(lessonRepository);
    }

    @Test
    void findAccessibleLessonIds_shouldRejectBlankRole() {
        assertThrows(
                IllegalArgumentException.class,
                () -> accessService.findAccessibleLessonIds(
                        "student@university.com",
                        " "
                )
        );

        verifyNoInteractions(lessonRepository);
    }
}
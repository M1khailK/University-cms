package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.customexceptions.GradeAccessDeniedException;
import ua.foxminded.university.customexceptions.GradeNotFoundException;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private StudentService studentService;
    @Mock
    private LessonService lessonService;
    @Mock
    private UserService userService;

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Test
    void gradeService_shouldCreateGrade_whenTeacherOwnsLesson() {
        Student student = createStudent();
        Lesson lesson = createLessonWithTeacher(2);

        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(2);
        when(lessonService.getById(100)).thenReturn(lesson);
        when(studentService.getById(1)).thenReturn(student);
        when(gradeRepository.save(any(Grade.class))).thenReturn(new Grade(500, student, lesson, 5));

        Grade actual = gradeService.createGrade(1, 100, 5, "teacher@example.com");

        assertEquals(500, actual.getId());
        assertEquals(5, actual.getValue());
        assertEquals(1, actual.getStudent().getId());
        assertEquals(100, actual.getLesson().getId());

        verify(gradeRepository).save(argThat(grade ->
                grade.getId() == null
                        && grade.getStudent().getId().equals(1)
                        && grade.getLesson().getId().equals(100)
                        && grade.getValue().equals(5)
        ));
    }

    @Test
    void gradeService_shouldThrowAccessDenied_whenTeacherDoesNotOwnLessonDuringCreate() {
        Lesson lesson = createLessonWithTeacher(2);

        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(99);
        when(lessonService.getById(100)).thenReturn(lesson);

        assertThrows(
                GradeAccessDeniedException.class,
                () -> gradeService.createGrade(1, 100, 5, "teacher@example.com")
        );

        verify(studentService, never()).getById(1);
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    void gradeService_shouldDeleteGrade_whenTeacherOwnsLesson() {
        Grade grade = createGradeForTeacher(2);

        when(gradeRepository.findById(500)).thenReturn(Optional.of(grade));
        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(2);

        gradeService.deleteGrade(500, "teacher@example.com");

        verify(gradeRepository).delete(grade);
    }

    @Test
    void gradeService_shouldThrowNotFound_whenDeletedGradeDoesNotExist() {
        when(gradeRepository.findById(500)).thenReturn(Optional.empty());

        assertThrows(
                GradeNotFoundException.class,
                () -> gradeService.deleteGrade(500, "teacher@example.com")
        );

        verify(gradeRepository, never()).delete(any(Grade.class));
    }

    @Test
    void gradeService_shouldThrowAccessDenied_whenTeacherDoesNotOwnLessonDuringDelete() {
        Grade grade = createGradeForTeacher(2);

        when(gradeRepository.findById(500)).thenReturn(Optional.of(grade));
        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(99);

        assertThrows(
                GradeAccessDeniedException.class,
                () -> gradeService.deleteGrade(500, "teacher@example.com")
        );

        verify(gradeRepository, never()).delete(any(Grade.class));
    }

    private Grade createGradeForTeacher(Integer teacherId) {
        return new Grade(500, createStudent(), createLessonWithTeacher(teacherId), 5);
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                group,
                "encoded-password",
                "STUDENT"
        );
    }

    private Lesson createLessonWithTeacher(Integer teacherId) {
        Teacher teacher = new Teacher(
                teacherId,
                "Bob",
                "Smith",
                "teacher@example.com",
                "encoded-password",
                "TEACHER"
        );

        Lesson lesson = new Lesson();
        lesson.setId(100);
        lesson.setName("Math lesson");
        lesson.setTeacher(teacher);
        lesson.setDate(LocalDate.of(2023, 4, 27));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(12, 0));

        return lesson;
    }
}
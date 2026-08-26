package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.foxminded.university.customexceptions.AttendanceAccessDeniedException;
import ua.foxminded.university.info.AttendanceRecord;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private LessonService lessonService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;


    @Test
    void attendanceService_shouldRecordAttendance_whenTeacherOwnsLesson() {
        Student student = createStudent();
        Lesson lesson = createLessonWithTeacher(2);

        when(lessonService.getById(100)).thenReturn(lesson);
        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(2);
        when(studentService.getById(1)).thenReturn(student);

        AttendanceRecord savedRecord = new AttendanceRecord();
        savedRecord.setRecordId(500);
        savedRecord.setStudent(student);
        savedRecord.setLesson(lesson);
        savedRecord.setAttendanceDate(LocalDate.of(2026, 8, 26));
        savedRecord.setAttendanceTime(LocalTime.of(10, 0));

        when(attendanceRepository.save(any(AttendanceRecord.class)))
                .thenReturn(savedRecord);

        AttendanceRecord actual = attendanceService.recordAttendance(
                1,
                100,
                LocalDate.of(2026, 8, 26),
                LocalTime.of(10, 0),
                "teacher@example.com"
        );

        assertEquals(500, actual.getRecordId());
        assertEquals(1, actual.getStudent().getId());
        assertEquals(100, actual.getLesson().getId());
        assertEquals(LocalDate.of(2026, 8, 26), actual.getAttendanceDate());
        assertEquals(LocalTime.of(10, 0), actual.getAttendanceTime());

        verify(attendanceRepository).save(argThat(record ->
                record.getRecordId() == null
                        && record.getStudent().getId().equals(1)
                        && record.getLesson().getId().equals(100)
                        && record.getAttendanceDate().equals(LocalDate.of(2026, 8, 26))
                        && record.getAttendanceTime().equals(LocalTime.of(10, 0))
        ));
    }

    @Test
    void attendanceService_shouldThrowAccessDenied_whenTeacherDoesNotOwnLesson() {
        Lesson lesson = createLessonWithTeacher(2);

        when(lessonService.getById(100)).thenReturn(lesson);
        when(userService.getUserIdByEmail("teacher@example.com")).thenReturn(99);

        assertThrows(
                AttendanceAccessDeniedException.class,
                () -> attendanceService.recordAttendance(
                        1,
                        100,
                        LocalDate.of(2026, 8, 26),
                        LocalTime.of(10, 0),
                        "teacher@example.com"
                )
        );

        verify(studentService, never()).getById(1);
        verify(attendanceRepository, never()).save(any(AttendanceRecord.class));
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
        lesson.setDate(LocalDate.of(2026, 8, 26));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(12, 0));

        return lesson;
    }
}
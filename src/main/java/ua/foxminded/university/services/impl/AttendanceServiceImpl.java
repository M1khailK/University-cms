package ua.foxminded.university.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.AttendanceAccessDeniedException;
import ua.foxminded.university.info.AttendanceRecord;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.services.AttendanceService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    private final LessonService lessonService;
    private final UserService userService;

    @Override
    @Transactional
    public AttendanceRecord recordAttendance(
            Integer studentId,
            Integer lessonId,
            LocalDate attendanceDate,
            LocalTime attendanceTime,
            String teacherEmail
    ) {
        Lesson lesson = lessonService.getById(lessonId);
        assertTeacherOwnsLesson(lesson, teacherEmail);

        Student student = studentService.getById(studentId);

        AttendanceRecord record = new AttendanceRecord();
        record.setStudent(student);
        record.setLesson(lesson);
        record.setAttendanceDate(attendanceDate);
        record.setAttendanceTime(attendanceTime);

        return attendanceRepository.save(record);
    }

    private void assertTeacherOwnsLesson(Lesson lesson, String teacherEmail) {
        int teacherId = userService.getUserIdByEmail(teacherEmail);

        if (lesson.getTeacher() == null
                || !Objects.equals(lesson.getTeacher().getId(), teacherId)) {
            throw new AttendanceAccessDeniedException(lesson.getId());
        }
    }
}
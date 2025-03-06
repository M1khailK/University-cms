package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.AttendanceRecord;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.AttendanceService;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public void recordAttendance(Integer studentId, Integer lessonId, LocalDate date, LocalTime time) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        AttendanceRecord record = new AttendanceRecord();
        record.setStudent(student);
        record.setLesson(lesson);
        record.setAttendanceDate(date);
        record.setAttendanceTime(time);
        attendanceRepository.save(record);
    }
}

package ua.foxminded.university.services;

import ua.foxminded.university.info.AttendanceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AttendanceService {

    AttendanceRecord recordAttendance(
            Integer studentId,
            Integer lessonId,
            LocalDate attendanceDate,
            LocalTime attendanceTime,
            String teacherEmail
    );
}
package ua.foxminded.university.services;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AttendanceService {
    void recordAttendance(Integer studentId, Integer lessonId, LocalDate date, LocalTime time);
}


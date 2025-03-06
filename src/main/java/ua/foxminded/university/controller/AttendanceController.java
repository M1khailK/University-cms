package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.foxminded.university.services.AttendanceService;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/recordAttendance")
    public ResponseEntity<String> recordAttendance(
            @RequestParam Integer studentId,
            @RequestParam Integer lessonId,
            @RequestParam String date,
            @RequestParam String time) {

        LocalDate attendanceDate = LocalDate.parse(date);
        LocalTime attendanceTime = LocalTime.parse(time);

        attendanceService.recordAttendance(studentId, lessonId, attendanceDate, attendanceTime);
        return ResponseEntity.ok("Attendance recorded successfully");
    }
}

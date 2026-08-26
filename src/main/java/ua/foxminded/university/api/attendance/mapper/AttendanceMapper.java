package ua.foxminded.university.api.attendance.mapper;

import org.springframework.stereotype.Component;
import ua.foxminded.university.api.attendance.dto.AttendanceResponse;
import ua.foxminded.university.info.AttendanceRecord;

@Component
public class AttendanceMapper {

    public AttendanceResponse toResponse(AttendanceRecord attendanceRecord) {
        return new AttendanceResponse(
                attendanceRecord.getRecordId(),
                attendanceRecord.getStudent().getId(),
                attendanceRecord.getLesson().getId(),
                attendanceRecord.getAttendanceDate(),
                attendanceRecord.getAttendanceTime()
        );
    }
}
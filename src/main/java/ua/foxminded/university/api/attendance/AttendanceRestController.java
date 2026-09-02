package ua.foxminded.university.api.attendance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.foxminded.university.api.attendance.dto.AttendanceCreateRequest;
import ua.foxminded.university.api.attendance.dto.AttendanceResponse;
import ua.foxminded.university.api.attendance.mapper.AttendanceMapper;
import ua.foxminded.university.services.AttendanceService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/attendance-records")
@RequiredArgsConstructor
public class AttendanceRestController {

    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;

    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendanceRecord(
            @Valid @RequestBody AttendanceCreateRequest request,
            Authentication authentication
    ) {
        AttendanceResponse response = attendanceMapper.toResponse(
                attendanceService.recordAttendance(
                        request.studentId(),
                        request.lessonId(),
                        request.attendanceDate(),
                        request.attendanceTime(),
                        authentication.getName()
                )
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
package ua.foxminded.university.api.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.attendance.mapper.AttendanceMapper;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.customexceptions.AttendanceAccessDeniedException;
import ua.foxminded.university.info.AttendanceRecord;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.AttendanceService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AttendanceRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({AttendanceMapper.class, ApiExceptionHandler.class})
class AttendanceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @Test
    void attendanceRestController_shouldCreateAttendanceRecord_whenRequestIsValid() throws Exception {
        when(attendanceService.recordAttendance(
                eq(1),
                eq(100),
                eq(LocalDate.of(2026, 8, 26)),
                eq(LocalTime.of(10, 0)),
                eq("teacher@example.com")
        )).thenReturn(createAttendanceRecord());

        mockMvc.perform(post("/api/v1/attendance-records")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 1,
                                  "lessonId": 100,
                                  "attendanceDate": "2026-08-26",
                                  "attendanceTime": "10:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(500))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.lessonId").value(100))
                .andExpect(jsonPath("$.attendanceDate").value("2026-08-26"))
                .andExpect(jsonPath("$.attendanceTime").exists());

        verify(attendanceService).recordAttendance(
                1,
                100,
                LocalDate.of(2026, 8, 26),
                LocalTime.of(10, 0),
                "teacher@example.com"
        );
    }

    @Test
    void attendanceRestController_shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/attendance-records")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": null,
                                  "lessonId": 100,
                                  "attendanceDate": "2026-08-26",
                                  "attendanceTime": "10:00"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void attendanceRestController_shouldReturnForbidden_whenTeacherDoesNotOwnLesson() throws Exception {
        when(attendanceService.recordAttendance(
                eq(1),
                eq(100),
                eq(LocalDate.of(2026, 8, 26)),
                eq(LocalTime.of(10, 0)),
                eq("teacher@example.com")
        )).thenThrow(new AttendanceAccessDeniedException(100));

        mockMvc.perform(post("/api/v1/attendance-records")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 1,
                                  "lessonId": 100,
                                  "attendanceDate": "2026-08-26",
                                  "attendanceTime": "10:00"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Attendance access denied"));
    }

    private AttendanceRecord createAttendanceRecord() {
        Student student = new Student();
        student.setId(1);

        Lesson lesson = new Lesson();
        lesson.setId(100);

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setRecordId(500);
        attendanceRecord.setStudent(student);
        attendanceRecord.setLesson(lesson);
        attendanceRecord.setAttendanceDate(LocalDate.of(2026, 8, 26));
        attendanceRecord.setAttendanceTime(LocalTime.of(10, 0));

        return attendanceRecord;
    }
}
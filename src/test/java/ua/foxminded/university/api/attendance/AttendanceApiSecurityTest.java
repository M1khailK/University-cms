package ua.foxminded.university.api.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.attendance.mapper.AttendanceMapper;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.AttendanceRecord;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.AttendanceService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AttendanceRestController.class)
@Import({SecurityConfig.class, JwtConfig.class, AttendanceMapper.class})
class AttendanceApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void attendanceApiSecurity_shouldAllowCreateAttendanceRecord_whenUserIsTeacher() throws Exception {
        when(attendanceService.recordAttendance(
                eq(1),
                eq(100),
                eq(LocalDate.of(2026, 8, 26)),
                eq(LocalTime.of(10, 0)),
                eq("teacher")
        )).thenReturn(createAttendanceRecord());

        mockMvc.perform(post("/api/v1/attendance-records")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated());
    }

    @Test
    void attendanceApiSecurity_shouldForbidCreateAttendanceRecord_whenUserIsStudent() throws Exception {
        mockMvc.perform(post("/api/v1/attendance-records")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void attendanceApiSecurity_shouldForbidCreateAttendanceRecord_whenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/attendance-records")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void attendanceApiSecurity_shouldRedirectToLogin_whenAnonymousCreatesAttendanceRecord() throws Exception {
        mockMvc.perform(post("/api/v1/attendance-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isUnauthorized());
    }

    private String validRequest() {
        return """
                {
                  "studentId": 1,
                  "lessonId": 100,
                  "attendanceDate": "2026-08-26",
                  "attendanceTime": "10:00"
                }
                """;
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
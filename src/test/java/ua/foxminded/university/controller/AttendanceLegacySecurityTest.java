package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.services.AttendanceService;

import javax.sql.DataSource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AttendanceController.class)
@Import(SecurityConfig.class)
class AttendanceLegacySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void attendanceLegacySecurity_shouldAllowRecordAttendance_whenUserIsTeacher() throws Exception {
        mockMvc.perform(post("/recordAttendance")
                        .with(user("teacher").roles("TEACHER"))
                        .param("studentId", "1")
                        .param("lessonId", "100")
                        .param("date", "2026-08-26")
                        .param("time", "10:00"))
                .andExpect(status().isOk());
    }

    @Test
    void attendanceLegacySecurity_shouldForbidRecordAttendance_whenUserIsStudent() throws Exception {
        mockMvc.perform(post("/recordAttendance")
                        .with(user("student").roles("STUDENT"))
                        .param("studentId", "1")
                        .param("lessonId", "100")
                        .param("date", "2026-08-26")
                        .param("time", "10:00"))
                .andExpect(status().isForbidden());
    }

    @Test
    void attendanceLegacySecurity_shouldForbidRecordAttendance_whenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/recordAttendance")
                        .with(user("admin").roles("ADMIN"))
                        .param("studentId", "1")
                        .param("lessonId", "100")
                        .param("date", "2026-08-26")
                        .param("time", "10:00"))
                .andExpect(status().isForbidden());
    }

    @Test
    void attendanceLegacySecurity_shouldRedirectToLogin_whenAnonymousRecordsAttendance() throws Exception {
        mockMvc.perform(post("/recordAttendance")
                        .param("studentId", "1")
                        .param("lessonId", "100")
                        .param("date", "2026-08-26")
                        .param("time", "10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
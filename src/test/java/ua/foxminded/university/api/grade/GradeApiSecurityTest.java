package ua.foxminded.university.api.grade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.grade.mapper.GradeMapper;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GradeService;

import javax.sql.DataSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GradeRestController.class)
@Import({SecurityConfig.class, GradeMapper.class})
class GradeApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradeService gradeService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void gradeApiSecurity_shouldAllowReadAllGrades_whenUserIsAdmin() throws Exception {
        when(gradeService.getAllGrades()).thenReturn(List.of(createGrade()));

        mockMvc.perform(get("/api/v1/grades")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void gradeApiSecurity_shouldAllowReadAllGrades_whenUserIsTeacher() throws Exception {
        when(gradeService.getAllGrades()).thenReturn(List.of(createGrade()));

        mockMvc.perform(get("/api/v1/grades")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());
    }

    @Test
    void gradeApiSecurity_shouldForbidReadAllGrades_whenUserIsStudent() throws Exception {
        mockMvc.perform(get("/api/v1/grades")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void gradeApiSecurity_shouldRedirectToLogin_whenAnonymousReadsAllGrades() throws Exception {
        mockMvc.perform(get("/api/v1/grades"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void gradeApiSecurity_shouldAllowReadOwnGrades_whenUserIsStudent() throws Exception {
        when(gradeService.getGradesByEmail("student")).thenReturn(List.of(createGrade()));

        mockMvc.perform(get("/api/v1/grades/me")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void gradeApiSecurity_shouldForbidReadOwnGrades_whenUserIsTeacher() throws Exception {
        mockMvc.perform(get("/api/v1/grades/me")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void gradeApiSecurity_shouldForbidReadOwnGrades_whenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/grades/me")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void gradeApiSecurity_shouldRedirectToLogin_whenAnonymousReadsOwnGrades() throws Exception {
        mockMvc.perform(get("/api/v1/grades/me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Grade createGrade() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        Student student = new Student(
                1,
                "Alice",
                "Brown",
                "student",
                group,
                "encoded-password",
                "STUDENT"
        );

        Lesson lesson = new Lesson();
        lesson.setId(100);
        lesson.setName("Math lesson");
        lesson.setDate(LocalDate.of(2023, 4, 27));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(12, 0));

        return new Grade(500, student, lesson, 5);
    }
}
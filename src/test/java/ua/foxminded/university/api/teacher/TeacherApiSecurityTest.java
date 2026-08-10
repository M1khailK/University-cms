package ua.foxminded.university.api.teacher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.teacher.mapper.TeacherMapperImpl;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeacherRestController.class)
@Import({SecurityConfig.class, TeacherMapperImpl.class, ApiExceptionHandler.class})
class TeacherApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void teacherApiSecurity_shouldAllowReadAllTeachers_whenUserIsAdmin() throws Exception {
        when(teacherService.getAll()).thenReturn(List.of(createTeacher()));

        mockMvc.perform(get("/api/v1/teachers")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void teacherApiSecurity_shouldAllowReadTeacherById_whenUserIsAdmin() throws Exception {
        when(teacherService.getById(1)).thenReturn(createTeacher());

        mockMvc.perform(get("/api/v1/teachers/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void teacherApiSecurity_shouldForbidReadAllTeachers_whenUserIsStudent() throws Exception {
        mockMvc.perform(get("/api/v1/teachers")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldForbidReadAllTeachers_whenUserIsTeacher() throws Exception {
        mockMvc.perform(get("/api/v1/teachers")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldRedirectToLogin_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/teachers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Teacher createTeacher() {
        return new Teacher(
                1,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );
    }
}
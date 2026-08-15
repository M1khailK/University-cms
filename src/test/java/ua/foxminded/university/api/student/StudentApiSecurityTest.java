package ua.foxminded.university.api.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.student.mapper.StudentMapperImpl;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.StudentService;

import javax.sql.DataSource;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentRestController.class)
@Import({SecurityConfig.class, StudentMapperImpl.class, ApiExceptionHandler.class})
class StudentApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void studentApiSecurity_shouldAllowReadAllStudents_whenUserIsAdmin() throws Exception {
        when(studentService.getAll()).thenReturn(List.of(createStudent()));

        mockMvc.perform(get("/api/v1/students")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void studentApiSecurity_shouldAllowReadStudentById_whenUserIsAdmin() throws Exception {
        when(studentService.getById(1)).thenReturn(createStudent());

        mockMvc.perform(get("/api/v1/students/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void studentApiSecurity_shouldForbidReadAllStudents_whenUserIsStudent() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentApiSecurity_shouldForbidReadAllStudents_whenUserIsTeacher() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentApiSecurity_shouldRedirectToLogin_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                group,
                "secret-password",
                "STUDENT"
        );
    }
}
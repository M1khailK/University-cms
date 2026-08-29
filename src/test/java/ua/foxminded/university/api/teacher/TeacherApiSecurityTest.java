package ua.foxminded.university.api.teacher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.teacher.mapper.TeacherMapperImpl;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import javax.sql.DataSource;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeacherRestController.class)
@Import({SecurityConfig.class, JwtConfig.class, TeacherMapperImpl.class, ApiExceptionHandler.class})
class TeacherApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private UserService userService;

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
                .andExpect(status().isUnauthorized());
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

    @Test
    void teacherApiSecurity_shouldAllowCreateTeacher_whenUserIsAdmin() throws Exception {
        when(teacherService.createTeacherAccount(
                eq("Alice"),
                eq("Brown"),
                eq("alice.brown@example.com")
        )).thenReturn(new Teacher(
                2,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                "encoded-password",
                "TEACHER"
        ));

        mockMvc.perform(post("/api/v1/teachers")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void teacherApiSecurity_shouldForbidCreateTeacher_whenUserIsStudent() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldForbidCreateTeacher_whenUserIsTeacher() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldRedirectToLogin_whenAnonymousCreatesTeacher() throws Exception {
        mockMvc.perform(post("/api/v1/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void teacherApiSecurity_shouldAllowUpdateTeacher_whenUserIsAdmin() throws Exception {
        when(teacherService.updateTeacherProfile(
                eq(1),
                eq("Robert"),
                eq("Johnson"),
                eq("robert.johnson@example.com")
        )).thenReturn(new Teacher(
                1,
                "Robert",
                "Johnson",
                "robert.johnson@example.com",
                "encoded-password",
                "TEACHER"
        ));

        mockMvc.perform(put("/api/v1/teachers/{id}", 1)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Robert",
                                  "lastName": "Johnson",
                                  "email": "robert.johnson@example.com"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void teacherApiSecurity_shouldForbidUpdateTeacher_whenUserIsStudent() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/{id}", 1)
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Robert",
                                  "lastName": "Johnson",
                                  "email": "robert.johnson@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldForbidUpdateTeacher_whenUserIsTeacher() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/{id}", 1)
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Robert",
                                  "lastName": "Johnson",
                                  "email": "robert.johnson@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldRedirectToLogin_whenAnonymousUpdatesTeacher() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Robert",
                                  "lastName": "Johnson",
                                  "email": "robert.johnson@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void teacherApiSecurity_shouldAllowDeactivateTeacher_whenUserIsAdmin() throws Exception {
        when(teacherService.getById(1)).thenReturn(createTeacher());

        mockMvc.perform(delete("/api/v1/teachers/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void teacherApiSecurity_shouldForbidDeactivateTeacher_whenUserIsStudent() throws Exception {
        mockMvc.perform(delete("/api/v1/teachers/{id}", 1)
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldForbidDeactivateTeacher_whenUserIsTeacher() throws Exception {
        mockMvc.perform(delete("/api/v1/teachers/{id}", 1)
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherApiSecurity_shouldRedirectToLogin_whenAnonymousDeactivatesTeacher() throws Exception {
        mockMvc.perform(delete("/api/v1/teachers/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

}
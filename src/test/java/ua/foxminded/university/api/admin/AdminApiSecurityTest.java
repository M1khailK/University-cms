package ua.foxminded.university.api.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.admin.mapper.AdminMapperImpl;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminRestController.class)
@Import({SecurityConfig.class, AdminMapperImpl.class, ApiExceptionHandler.class})
class AdminApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void adminApiSecurity_shouldAllowCreateAdmin_whenUserIsAdmin() throws Exception {
        when(teacherService.createAdminAccount(
                eq("Alice"),
                eq("Root"),
                eq("alice.root@example.com")
        )).thenReturn(createAdmin());

        mockMvc.perform(post("/api/v1/admins")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void adminApiSecurity_shouldForbidCreateAdmin_whenUserIsStudent() throws Exception {
        mockMvc.perform(post("/api/v1/admins")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminApiSecurity_shouldForbidCreateAdmin_whenUserIsTeacher() throws Exception {
        mockMvc.perform(post("/api/v1/admins")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminApiSecurity_shouldRedirectToLogin_whenAnonymousCreatesAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Teacher createAdmin() {
        return new Teacher(
                100,
                "Alice",
                "Root",
                "alice.root@example.com",
                "encoded-password",
                "ADMIN"
        );
    }
}
package ua.foxminded.university.api.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.services.AssistantService;

import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssistantRestController.class)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
        ApiExceptionHandler.class
})
class AssistantApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService assistantService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void assistantApiSecurity_shouldAllowRequest_whenUserIsAdmin()
            throws Exception {

        when(assistantService.answer("Hello"))
                .thenReturn("Hello from assistant");

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer")
                        .value("Hello from assistant"));
    }

    @Test
    void assistantApiSecurity_shouldAllowRequest_whenUserIsTeacher()
            throws Exception {

        when(assistantService.answer("Hello"))
                .thenReturn("Hello from assistant");

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void assistantApiSecurity_shouldAllowRequest_whenUserIsStudent()
            throws Exception {

        when(assistantService.answer("Hello"))
                .thenReturn("Hello from assistant");

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void assistantApiSecurity_shouldForbidRequest_whenUserHasUnsupportedRole()
            throws Exception {

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .with(user("guest").roles("GUEST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void assistantApiSecurity_shouldReturnUnauthorized_whenUserIsAnonymous()
            throws Exception {

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}
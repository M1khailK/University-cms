package ua.foxminded.university.api.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.services.AuthService;
import ua.foxminded.university.services.model.AccessToken;

import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class AuthApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void login_shouldBeAccessibleWithoutAuthentication() throws Exception {
        when(authService.login(
                "student@example.com",
                "password"
        )).thenReturn(
                new AccessToken("jwt-token", 900)
        );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "student@example.com",
                                          "password": "password"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("jwt-token"));
    }

    @Test
    void login_shouldReturnValidationProblemDetail_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "invalid-email",
                                          "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.detail")
                        .value("Request validation failed"))
                .andExpect(jsonPath("$.errors.email").isArray())
                .andExpect(jsonPath("$.errors.email[0]").isString())
                .andExpect(jsonPath("$.errors.password").isArray())
                .andExpect(jsonPath("$.errors.password[0]").isString());
    }

    @Test
    void login_shouldReturnProblemDetail_whenJsonIsMalformed()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "email": "student@example.com",
                                      "password":
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title")
                        .value("Malformed request"))
                .andExpect(jsonPath("$.detail")
                        .value("Request body is malformed"));
    }

    @Test
    void login_shouldReturnProblemDetail_whenContentTypeIsUnsupported()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.TEXT_PLAIN)
                                .content("""
                                    {
                                      "email": "student@example.com",
                                      "password": "password"
                                    }
                                    """)
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.title")
                        .value("Unsupported media type"))
                .andExpect(jsonPath("$.detail")
                        .value("Request content type is not supported"));
    }
}
package ua.foxminded.university.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ua.foxminded.university.config.JwtProperties;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {

    private static final int USER_ID = 900_001;
    private static final String EMAIL = "jwt.integration@university.test";
    private static final String PASSWORD = "StrongPassword123!";
    private static final String ROLE = "STUDENT";

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        removeTestUser();

        jdbcTemplate.update(
                """
                        INSERT INTO users (
                            user_id,
                            first_name,
                            last_name,
                            email,
                            password,
                            isEnabled
                        )
                        VALUES (?, ?, ?, ?, ?, true)
                        """,
                USER_ID,
                "JWT",
                "Integration",
                EMAIL,
                passwordEncoder.encode(PASSWORD)
        );

        jdbcTemplate.update(
                """
                        INSERT INTO user_role (user_id, role)
                        VALUES (?, ?)
                        """,
                USER_ID,
                ROLE
        );

        jdbcTemplate.update(
                """
                        INSERT INTO students (user_id, group_id)
                        VALUES (?, NULL)
                        """,
                USER_ID
        );
    }

    @AfterEach
    void tearDown() {
        removeTestUser();
    }

    @Test
    void authLogin_shouldReturnSignedJwt_whenCredentialsAreValid()
            throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest(PASSWORD))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        JsonNode response = objectMapper.readTree(
                result.getResponse().getContentAsString()
        );

        String accessToken = response
                .get("accessToken")
                .asText();

        assertThat(accessToken).isNotBlank();

        assertThat(response.get("expiresIn").asLong())
                .isEqualTo(jwtProperties.accessTokenTtl().toSeconds());

        Jwt jwt = jwtDecoder.decode(accessToken);

        assertThat(jwt.getSubject())
                .isEqualTo(EMAIL);

        assertThat(jwt.getClaimAsString("iss"))
                .isEqualTo(jwtProperties.issuer());

        assertThat(jwt.getClaimAsStringList("authorities"))
                .containsExactly("ROLE_STUDENT");
    }

    @Test
    void authLogin_shouldReturnUnauthorized_whenPasswordIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest("wrong-password"))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title")
                        .value("Authentication failed"))
                .andExpect(jsonPath("$.detail")
                        .value("Invalid email or password"));
    }

    @Test
    void authLogin_shouldReturnUnauthorized_whenUserIsDisabled()
            throws Exception {

        jdbcTemplate.update(
                "UPDATE users SET isEnabled = false WHERE user_id = ?",
                USER_ID
        );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest(PASSWORD))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title")
                        .value("Authentication failed"))
                .andExpect(jsonPath("$.detail")
                        .value("Invalid email or password"));
    }

    @Test
    void authLogin_shouldAuthorizeProfileRequest_whenIssuedTokenIsUsedAsBearer()
            throws Exception {

        MvcResult loginResult = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest(PASSWORD))
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginResponse = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        );

        String accessToken = loginResponse
                .get("accessToken")
                .asText();

        mockMvc.perform(
                        get("/api/v1/profile")
                                .header(
                                        "Authorization",
                                        "Bearer " + accessToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ));
    }

    private String loginRequest(String password) throws Exception {
        return objectMapper.writeValueAsString(
                Map.of(
                        "email", EMAIL,
                        "password", password
                )
        );
    }

    private void removeTestUser() {
        jdbcTemplate.update(
                "DELETE FROM user_role WHERE user_id = ?",
                USER_ID
        );

        jdbcTemplate.update(
                "DELETE FROM students WHERE user_id = ?",
                USER_ID
        );

        jdbcTemplate.update(
                "DELETE FROM users WHERE user_id = ? OR email = ?",
                USER_ID,
                EMAIL
        );
    }
}
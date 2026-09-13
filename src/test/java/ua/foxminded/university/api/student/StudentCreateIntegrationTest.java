package ua.foxminded.university.api.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.services.PasswordService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15:///student-create-integration",
        "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
class StudentCreateIntegrationTest {

    private static final int EXISTING_USER_ID = 900_101;
    private static final int GROUP_ID = 900_101;
    private static final String EMAIL = "duplicate.student@university.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private PasswordService passwordService;
    @MockitoBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        removeTestData();

        jdbcTemplate.update(
                """
                        INSERT INTO groups (group_id, group_name)
                        VALUES (?, ?)
                        """,
                GROUP_ID,
                "TEST-GROUP"
        );

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
                EXISTING_USER_ID,
                "Existing",
                "Student",
                EMAIL,
                "existing-password"
        );

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setPassword("encoded-generated-password");
            return null;
        }).when(passwordService)
                .generateAndSendPasswordForUser(any(User.class));
    }

    @AfterEach
    void tearDown() {
        removeTestData();
    }

    @Test
    void createStudent_shouldReturnConflict_whenEmailAlreadyExists()
            throws Exception {

        mockMvc.perform(post("/api/v1/students")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "duplicate.student@university.test",
                                  "groupId": 900101
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Duplicate email"));
    }

    private void removeTestData() {
        jdbcTemplate.update(
                "DELETE FROM user_role WHERE user_id = ?",
                EXISTING_USER_ID
        );

        jdbcTemplate.update(
                "DELETE FROM students WHERE user_id = ?",
                EXISTING_USER_ID
        );

        jdbcTemplate.update(
                "DELETE FROM users WHERE user_id = ? OR email = ?",
                EXISTING_USER_ID,
                EMAIL
        );

        jdbcTemplate.update(
                "DELETE FROM groups WHERE group_id = ?",
                GROUP_ID
        );
    }
}
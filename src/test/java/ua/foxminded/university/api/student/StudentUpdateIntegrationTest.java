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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:15:///student-update-integration",
        "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
class StudentUpdateIntegrationTest {

    private static final int STUDENT_ID = 900_201;
    private static final int CONFLICTING_USER_ID = 900_202;
    private static final int GROUP_ID = 900_201;

    private static final String ORIGINAL_EMAIL =
            "student.to.update@university.test";

    private static final String CONFLICTING_EMAIL =
            "already.used@university.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                "UPDATE-TEST-GROUP"
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
                STUDENT_ID,
                "Original",
                "Student",
                ORIGINAL_EMAIL,
                "password"
        );

        jdbcTemplate.update(
                """
                        INSERT INTO user_role (user_id, role)
                        VALUES (?, ?)
                        """,
                STUDENT_ID,
                "STUDENT"
        );

        jdbcTemplate.update(
                """
                        INSERT INTO students (user_id, group_id)
                        VALUES (?, ?)
                        """,
                STUDENT_ID,
                GROUP_ID
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
                CONFLICTING_USER_ID,
                "Existing",
                "User",
                CONFLICTING_EMAIL,
                "password"
        );
    }

    @AfterEach
    void tearDown() {
        removeTestData();
    }

    @Test
    void updateStudent_shouldReturnConflict_whenEmailAlreadyExists()
            throws Exception {

        mockMvc.perform(put("/api/v1/students/{id}", STUDENT_ID)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Updated",
                                  "lastName": "Student",
                                  "email": "already.used@university.test",
                                  "groupId": 900201
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
                "DELETE FROM user_role WHERE user_id IN (?, ?)",
                STUDENT_ID,
                CONFLICTING_USER_ID
        );

        jdbcTemplate.update(
                "DELETE FROM students WHERE user_id IN (?, ?)",
                STUDENT_ID,
                CONFLICTING_USER_ID
        );

        jdbcTemplate.update(
                """
                        DELETE FROM users
                        WHERE user_id IN (?, ?)
                           OR email IN (?, ?)
                        """,
                STUDENT_ID,
                CONFLICTING_USER_ID,
                ORIGINAL_EMAIL,
                CONFLICTING_EMAIL
        );

        jdbcTemplate.update(
                "DELETE FROM groups WHERE group_id = ?",
                GROUP_ID
        );
    }
}
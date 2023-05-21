package ua.foxminded.university.repository.impl;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.foxminded.university.repository.UserRepository;

@SpringBootTest
public class UserRepositoryImplTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE students, users, groups, subjects, teachers,admins, lessons, user_role RESTART IDENTITY;");
        jdbcTemplate.execute("INSERT INTO users (first_name, last_name, email, password) VALUES " +
                "('Alex', 'First', 'alex.first@example.com', 'password');");
        jdbcTemplate.execute("INSERT INTO students (user_id) VALUES (1);");
        jdbcTemplate.execute("INSERT INTO user_role (user_id, role) VALUES (1, 'STUDENT')");
    }

    @Test
    public void userRepository_shouldDeactivateUserAccount_whenInputHasUserId() {
        userRepository.deactivateUserAccountById(1);
        String selectIsEnabledFromStudent = "SELECT u.isEnabled " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "WHERE s.user_id = ?";

        boolean actual = jdbcTemplate.queryForObject(selectIsEnabledFromStudent, Boolean.class, 1);

        Assertions.assertEquals(false, actual);
    }
}

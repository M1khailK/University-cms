package ua.foxminded.university.repository.impl;

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
        jdbcTemplate.execute("TRUNCATE TABLE users, groups, subjects, teachers, students, lessons, user_role;");
        jdbcTemplate.execute("ALTER SEQUENCE user_seq RESTART WITH 1;");
        jdbcTemplate.execute("INSERT INTO users (user_id,first_name, last_name, email, password) VALUES " +
                "(nextval('user_seq'), 'Alex', 'First', 'alex.first@example.com', 'password');");
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

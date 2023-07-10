package ua.foxminded.university.repository.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.config.repository.RepositoriesTestConfig;
import ua.foxminded.university.repository.UserRepository;

import java.util.Optional;

@SpringBootTest
@EnableAutoConfiguration
@Sql(scripts = "/test_data.sql")
@ContextConfiguration(classes = {RepositoriesTestConfig.class, EntityManager.class})
public class UserRepositoryImplTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void userRepository_shouldDeactivateUserAccount_whenInputHasUserId() {
        userRepository.deactivateUserAccountById(1);
        String selectIsEnabledFromStudent = "SELECT u.isEnabled " +
                "FROM students s " +
                "JOIN users u ON s.user_id = u.user_id " +
                "WHERE s.user_id = ?";

        boolean actual = jdbcTemplate.queryForObject(selectIsEnabledFromStudent, Boolean.class, 1);

        Assertions.assertFalse(actual);
    }

    @Test
    public void userRepository_shouldFindUserId_whenInputHasUserEmail() {
        Optional<Integer> id = userRepository.findUserIdByEmail("alex.1@example.com");
        Assertions.assertEquals(Optional.of(1), id);
    }
}

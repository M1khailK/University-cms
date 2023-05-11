package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.foxminded.university.info.Teacher;

import java.util.Optional;

@SpringBootTest
public class TeacherRepositoryTest {

    private static final String EMAIL = "bob.second@example.com";

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE students, users, groups, subjects, teachers, lessons, user_role RESTART IDENTITY;");
        jdbcTemplate.execute("INSERT INTO users (first_name, last_name, email, password) VALUES ('Bob', 'Second', 'bob.second@example.com', 'password');");
        jdbcTemplate.execute("INSERT INTO teachers (user_id) VALUES (1);");
        jdbcTemplate.execute("INSERT INTO user_role (user_id, role) VALUES (1, 'TEACHER');");
    }

    @Test
    public void teacherRepository_shouldReturnTeacherByEmail_whenInputHasEmail() {
        Teacher example = new Teacher(1, "Bob", "Second", EMAIL);
        Optional<Teacher> actual = teacherRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(example), actual);
    }

    @Test
    public void teacherRepository_shouldReturnPassword_whenInputHasTeacherId() {
        String example = "password";
        String actual = teacherRepository.findPasswordById(1);
        Assertions.assertEquals(example, actual);
    }

    @Test
    public void teacherRepository_shouldChangePassword_whenInputHasNewPasswordAndTeacherId() {
        String example = "newPassword";
        teacherRepository.changePasswordById("newPassword", 1);

        String actual = teacherRepository.findPasswordById(1);
        Assertions.assertEquals(example, actual);
    }
}

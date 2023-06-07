package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;

import java.util.List;
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
        jdbcTemplate.execute("TRUNCATE TABLE users, groups, subjects, teachers, students, lessons, user_role;");
        jdbcTemplate.execute("ALTER SEQUENCE user_seq RESTART WITH 1;");
        jdbcTemplate.execute("INSERT INTO users (user_id,first_name, last_name, email, password) VALUES " +
                "(nextval('user_seq'),'Bob', 'Second', 'bob.second@example.com', 'password')," +
                "(nextval('user_seq'),'Alex','Third','alex.third@example.com','password');");
        jdbcTemplate.execute("INSERT INTO teachers (user_id) VALUES (1),(2);");
        jdbcTemplate.execute("INSERT INTO user_role (user_id, role) VALUES (1, 'TEACHER'),(2,'TEACHER');");
    }

    @Test
    public void teacherRepository_shouldReturnTeacherByEmail_whenInputHasEmail() {
        Teacher expected = new Teacher(1, "Bob", "Second", EMAIL,"password");
        Optional<Teacher> actual = teacherRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(expected), actual);
    }

    @Test
    public void teacherRepository_shouldReturnPassword_whenInputHasUserId() {
        String expected = "password";
        String actual = teacherRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void teacherRepository_shouldChangePassword_whenInputHasNewPasswordAndUserId() {
        String expected = "newPassword";
        teacherRepository.changePasswordById("newPassword", 1);

        String actual = teacherRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void teacherRepository_shouldReturnTeacher_whenInputHasUserId() {
        Teacher expected = new Teacher(1, "Bob", "Second", EMAIL,"password");
        Teacher actual = teacherRepository.findById(1).get();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void teacherRepository_shouldReturnListOfTeachers_whenTheirAccountsAreEnabled() {
        String disableUserAccountQuery = "UPDATE users SET isEnabled = FALSE WHERE user_id = ?";

        jdbcTemplate.update(disableUserAccountQuery, 1);
        List<Teacher> actual = teacherRepository.findAllEnabledTeachers();
        List<Teacher> expected = List.of(new Teacher(2,"Alex","Third","alex.third@example.com","password"));
        Assertions.assertEquals(expected, actual);
    }
}

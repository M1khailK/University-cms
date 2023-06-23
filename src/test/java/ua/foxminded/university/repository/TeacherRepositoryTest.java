package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.info.Teacher;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Sql(scripts = "/test_data.sql")
public class TeacherRepositoryTest {

    private static final String EMAIL = "bob.3@example.com";

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void teacherRepository_shouldReturnTeacherByEmail_whenInputHasEmail() {
        List<Teacher> teachers = teacherRepository.findAll();
        teachers.forEach(System.out::println);
        Teacher expected = new Teacher(3, "Bob", "Third", EMAIL, "password","TEACHER");
        Optional<Teacher> actual = teacherRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(expected), actual);
    }

    @Test
    public void teacherRepository_shouldReturnPassword_whenInputHasUserId() {
        Optional<String> expected = Optional.of("password");
        Optional<String> actual = teacherRepository.findPasswordById(3);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void teacherRepository_shouldChangePassword_whenInputHasNewPasswordAndUserId() {
        Optional<String> expected = Optional.of("newPassword");
        teacherRepository.changePasswordById("newPassword", 3);
        Optional<String> actual = teacherRepository.findPasswordById(3);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void teacherRepository_shouldReturnTeacher_whenInputHasUserId() {
        Teacher expected = new Teacher(3, "Bob", "Third", EMAIL, "password","TEACHER");
        Teacher actual = teacherRepository.findById(3).get();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void teacherRepository_shouldReturnListOfTeachers_whenTheirAccountsAreEnabled() {
        List<Teacher> actual = teacherRepository.findAllEnabledTeachers();
        List<Teacher> expected = List.of(new Teacher(4, "Alex", "Fourth", "alex.4@example.com", "password","TEACHER"));
        Assertions.assertEquals(expected, actual);
    }
}

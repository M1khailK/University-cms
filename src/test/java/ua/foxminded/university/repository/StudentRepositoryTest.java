package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.foxminded.university.info.Student;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class StudentRepositoryTest {

    private static final String EMAIL = "alex.first@example.com";

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE students, users, groups, subjects, teachers,admins, lessons, user_role RESTART IDENTITY;");
        jdbcTemplate.execute("INSERT INTO users (first_name, last_name, email, password) VALUES ('Alex', 'First', 'alex.first@example.com', 'password'),('Bob','Second','bob.second@example.com','password');");
        jdbcTemplate.execute("INSERT INTO students (user_id) VALUES (1),(2);");
        jdbcTemplate.execute("INSERT INTO user_role (user_id, role) VALUES (1, 'STUDENT'),(2,'STUDENT');");
    }

    @Test
    public void studentRepository_shouldReturnStudentByEmail_whenInputHasEmail() {
        Student expected = new Student(1, "Alex", "First", EMAIL, null);
        Optional<Student> actual = studentRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(expected), actual);
    }

    @Test
    public void studentRepository_shouldReturnPassword_whenInputHasStudentId() {
        String expected = "password";

        String actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldReturnUserId_whenInputHasStudentEmail() {
        String email = "alex.first@example.com";
        Integer actual = studentRepository.findIdByEmail(email);
        Integer expected = 1;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldChangePassword_whenInputHasNewPasswordAndStudentId() {
        String expected = "newPassword";
        studentRepository.changePasswordById("newPassword", 1);

        String actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldReturnStudent_whenInputHasUserId() {
        Student expected = new Student(1, "Alex", "First", EMAIL, null);
        Student actual = studentRepository.findStudentByUserId(1).get();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldReturnListOfStudents_whenTheirAccountsAreEnabled() {
        String disableUserAccountQuery = "UPDATE users SET isEnabled = FALSE WHERE user_id = ?";

        jdbcTemplate.update(disableUserAccountQuery, 1);
        List<Student> actual = studentRepository.findAllEnabledStudents();
        List<Student> expected = List.of(new Student(2,"Bob","Second","bob.second@example.com",null));
        Assertions.assertEquals(expected, actual);
    }

}

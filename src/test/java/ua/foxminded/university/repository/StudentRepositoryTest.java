package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import ua.foxminded.university.config.repository.RepositoriesTestConfig;
import ua.foxminded.university.info.Student;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Sql(scripts = {"/test_data.sql"})
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = RepositoriesTestConfig.class)
public class StudentRepositoryTest {

    private static final String EMAIL = "bob.2@example.com";

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void studentRepository_shouldReturnStudentByEmail_whenInputHasEmail() {
        Student expected = new Student(2, "Bob", "Second", EMAIL, null, "password","STUDENT");
        Optional<Student> actual = studentRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(expected), actual);
    }

    @Test
    public void studentRepository_shouldReturnPassword_whenInputHasStudentId() {
        Optional<String> expected = Optional.of("password");

        Optional<String> actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldChangePassword_whenInputHasNewPasswordAndStudentId() {
        Optional<String> expected = Optional.of("newPassword");
        studentRepository.changePasswordById("newPassword", 1);

        Optional<String> actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void studentRepository_shouldReturnListOfStudents_whenTheirAccountsAreEnabled() {
       List<Student> actual = studentRepository.findAll();
       List<Student> expected = List.of(new Student(2, "Bob", "Second", "bob.2@example.com", null, "password","STUDENT"));
        Assertions.assertEquals(expected, actual);
    }

}

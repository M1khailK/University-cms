package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.info.Student;

import java.util.Optional;

@SpringBootTest
@Transactional
public class StudentRepositoryTest {

    private static final String EMAIL = "alex.first@example.com";

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void studentRepository_shouldReturnStudentByEmail_whenInputHasEmail() {
        Student example = new Student(1, "Alex", "First", EMAIL, null);
        Optional<Student> actual = studentRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(example), actual);
    }

    @Test
    public void studentRepository_shouldReturnPassword_whenInputHasStudentId() {
        String example = "password";

        String actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(example, actual);
    }

    @Test
    public void studentRepository_shouldChangePassword_whenInputHasNewPasswordAndStudentId() {
        String example = "newPassword";
        studentRepository.changePasswordById("newPassword", 1);

        String actual = studentRepository.findPasswordById(1);
        Assertions.assertEquals(example, actual);
    }
}

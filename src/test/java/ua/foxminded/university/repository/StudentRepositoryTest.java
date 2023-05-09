package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.foxminded.university.info.Student;

import java.util.Optional;

@SpringBootTest
public class StudentRepositoryTest {

    private static final String EMAIL = "alex.second@example.com";

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void studentRepository_shouldReturnStudentByEmail_whenInputHasEmail() {
        Student example = new Student(1,"Alex", "Second", EMAIL, null);

        Optional<Student> actual = studentRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(example), actual);
    }
}

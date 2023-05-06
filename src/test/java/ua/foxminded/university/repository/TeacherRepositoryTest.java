package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ua.foxminded.university.info.Teacher;

import java.util.Optional;

@SpringBootTest
public class TeacherRepositoryTest {

    private static final String EMAIL = "bob.first@example.com";

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    public void teacherRepository_shouldReturnTeacherByEmail_whenInputHasEmail() {
        Teacher example = new Teacher(1, "Bob", "First", EMAIL);
        Optional<Teacher> actual = teacherRepository.findByEmail(EMAIL);
        Assertions.assertEquals(Optional.of(example), actual);
    }
}

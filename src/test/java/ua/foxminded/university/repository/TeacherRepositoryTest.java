package ua.foxminded.university.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.info.Teacher;

import java.util.Optional;

@SpringBootTest
@Transactional
public class TeacherRepositoryTest {

    private static final String EMAIL = "bob.second@example.com";

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    public void teacherRepository_shouldReturnTeacherByEmail_whenInputHasEmail() {
        Teacher example = new Teacher(1,"Bob", "Second", EMAIL);
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

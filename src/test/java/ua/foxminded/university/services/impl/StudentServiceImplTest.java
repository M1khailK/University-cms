package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.service.ServicesTestConfig;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.StudentService;

import java.nio.CharBuffer;
import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@MockBean(LessonRepository.class)
@MockBean(TeacherRepository.class)
@MockBean(Clock.class)
@ContextConfiguration(classes = ServicesTestConfig.class)
public class StudentServiceImplTest {

    private static final String EMAIL = "student@example.com";
    private static final char[] PASSWORD = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};

    private static final char[] NEW_PASS = new char[]{'n', 'e', 'w', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'};

    private static final int ID = 1;
    private static final Student student = new Student(ID, "Alex", "First", EMAIL, null, "password","STUDENT");

    @Autowired
    private StudentService studentService;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));
        when(studentRepository.findPasswordById(student.getId())).thenReturn(Optional.of(Arrays.toString(PASSWORD)));
    }

    @Test
    public void studentService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        char[] oldPassword = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        char[] newPassword = new char[]{'n', 'e', 'w', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        Student student = new Student(ID, "Alex", "First", EMAIL, null, "password","STUDENT");

        when(passwordEncoder.matches(CharBuffer.wrap(oldPassword), studentRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id")))).thenReturn(true);
        when(passwordEncoder.encode(CharBuffer.wrap(newPassword))).thenReturn(Arrays.toString(newPassword));
        doNothing().when(studentRepository).changePasswordById(Arrays.toString(newPassword), student.getId());

        studentService.changePassword(EMAIL, oldPassword, newPassword);

        verify(studentRepository).findByEmail(EMAIL);
        verify(studentRepository, times(2)).findPasswordById(ID);
        verify(passwordEncoder).matches(CharBuffer.wrap(oldPassword), studentRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id")));
        verify(studentRepository).changePasswordById(Arrays.toString(newPassword), student.getId());
        verify(studentRepository).save(student);

    }

    @Test
    public void studentService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchStudentPassword() {
        when(passwordEncoder.matches(Arrays.toString(PASSWORD), studentRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id")))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> studentService.changePassword(EMAIL, PASSWORD, NEW_PASS));
    }
}

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

import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@MockBean(LessonRepository.class)
@MockBean(TeacherRepository.class)
@MockBean(Clock.class)
@ContextConfiguration(classes = ServicesTestConfig.class)
public class StudentServiceImplTest {

    private static final String EMAIL = "student@example.com";
    private static final char[] PASSWORD = "password".toCharArray();

    private static final char[] NEW_PASS = "newPassword".toCharArray();

    private static final int ID = 1;
    private static final Student student = new Student(ID, "Alex", "First", EMAIL, null, "password", "STUDENT");

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
        char[] oldPassword = "oldPassword".toCharArray();
        char[] newPassword = "newPassword".toCharArray();

        Student student = new Student(ID, "Alex", "First", EMAIL, null, "encodedOldPassword", "STUDENT");

        when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));
        when(studentRepository.findPasswordById(ID)).thenReturn(Optional.of("encodedOldPassword"));

        when(passwordEncoder.matches(any(CharSequence.class), eq("encodedOldPassword")))
                .thenReturn(true);

        when(passwordEncoder.encode(any(CharSequence.class)))
                .thenReturn("encodedNewPassword");

        studentService.changePassword(EMAIL, oldPassword, newPassword);

        verify(studentRepository).findByEmail(EMAIL);
        verify(studentRepository).findPasswordById(ID);
        verify(passwordEncoder).matches(any(CharSequence.class), eq("encodedOldPassword"));
        verify(passwordEncoder).encode(any(CharSequence.class));
        verify(studentRepository).changePasswordById("encodedNewPassword", ID);
    }

    @Test
    public void studentService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchStudentPassword() {
        when(passwordEncoder.matches(Arrays.toString(PASSWORD), studentRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by student's id")))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> studentService.changePassword(EMAIL, PASSWORD, NEW_PASS));
    }
}

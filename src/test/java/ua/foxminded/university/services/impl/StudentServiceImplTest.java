package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.services.StudentService;

import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class StudentServiceImplTest {

    private static final String EMAIL = "student@example.com";
    private static final String PASSWORD = "password";
    private static final String newPassword = "newPassword";
    private static final int ID = 1;
    private static final Student student = new Student(ID, "Alex", "First", EMAIL, null);

    @Autowired
    private StudentService studentService;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        lenient().when(studentRepository.findByEmail(EMAIL)).thenReturn(Optional.of(student));
        lenient().when(studentRepository.findPasswordById(student.getId())).thenReturn(PASSWORD);
    }

    @Test
    public void studentService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        String oldPassword = "password";
        String newPassword = "newPassword";
        Student student = new Student(ID, "Alex", "First", EMAIL, null);

        lenient().when(passwordEncoder.matches(oldPassword, studentRepository.findPasswordById(ID))).thenReturn(true);
        lenient().when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
        lenient().doNothing().when(studentRepository).changePasswordById(newPassword, student.getId());

        studentService.changePassword(EMAIL, oldPassword, newPassword);

        verify(studentRepository).findByEmail(EMAIL);
        verify(studentRepository, times(2)).findPasswordById(ID);
        verify(passwordEncoder).matches(oldPassword, studentRepository.findPasswordById(ID));
        verify(studentRepository).changePasswordById(newPassword, student.getId());
        verify(studentRepository).save(student);

    }

    @Test
    public void studentService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchStudentPassword() {
        lenient().when(passwordEncoder.matches(PASSWORD, studentRepository.findPasswordById(ID))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> studentService.changePassword(EMAIL, PASSWORD, newPassword));
    }
}

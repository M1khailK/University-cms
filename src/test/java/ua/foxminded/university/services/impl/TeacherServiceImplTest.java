package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.TeacherService;

import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class TeacherServiceImplTest {

    private static final String EMAIL = "teacher@example.com";
    private static final String PASSWORD = "password";
    private static final String newPassword = "newPassword";
    private static final int ID = 1;
    private static final Teacher teacher = new Teacher(ID, "Bob", "First", EMAIL);


    @Autowired
    private TeacherService teacherService;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        lenient().when(teacherRepository.findByEmail(EMAIL)).thenReturn(Optional.of(teacher));
        lenient().when(teacherRepository.findPasswordById(teacher.getId())).thenReturn(PASSWORD);
    }

    @Test
    public void teacherService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        lenient().when(passwordEncoder.matches(PASSWORD, teacherRepository.findPasswordById(ID))).thenReturn(true);
        lenient().when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
        lenient().doNothing().when(teacherRepository).changePasswordById(newPassword, teacher.getId());

        teacherService.changePassword(EMAIL, PASSWORD, newPassword);

        verify(teacherRepository).findByEmail(EMAIL);
        verify(teacherRepository, times(2)).findPasswordById(ID);
        verify(passwordEncoder).matches(PASSWORD, teacherRepository.findPasswordById(ID));
        verify(teacherRepository).changePasswordById(newPassword, teacher.getId());
        verify(teacherRepository).save(teacher);

    }

    @Test
    public void teacherService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchTeacherPassword() {
        lenient().when(passwordEncoder.matches(PASSWORD, teacherRepository.findPasswordById(ID))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> teacherService.changePassword(EMAIL, PASSWORD, newPassword));
    }
}

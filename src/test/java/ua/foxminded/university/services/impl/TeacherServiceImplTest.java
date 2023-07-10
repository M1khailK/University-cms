package ua.foxminded.university.services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import ua.foxminded.university.config.service.ServicesTestConfig;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.TeacherService;

import java.nio.CharBuffer;
import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@MockBean(LessonRepository.class)
@MockBean(StudentRepository.class)
@MockBean(Clock.class)
@ContextConfiguration(classes = ServicesTestConfig.class)
public class TeacherServiceImplTest {

    private static final String EMAIL = "teacher@example.com";
    private static final char[] PASSWORD = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};

    private static final char[] NEW_PASS = new char[]{'n', 'e', 'w', 'P', 'a', 's', 's', 'w', 'o', 'r', 'd'};
    private static final int ID = 1;
    private static final Teacher teacher = new Teacher(ID, "Bob", "First", EMAIL,"password","TEACHER");


    @Autowired
    private TeacherService teacherService;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        when(teacherRepository.findByEmail(EMAIL)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findPasswordById(teacher.getId())).thenReturn(Optional.of(Arrays.toString(PASSWORD)));
    }

    @Test
    public void teacherService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        when(passwordEncoder.matches(CharBuffer.wrap(PASSWORD), teacherRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by teacher's id")))).thenReturn(true);
        when(passwordEncoder.encode(CharBuffer.wrap(NEW_PASS))).thenReturn(Arrays.toString(NEW_PASS));
        doNothing().when(teacherRepository).changePasswordById(Arrays.toString(NEW_PASS), teacher.getId());

        ArgumentCaptor<char[]> newPasswordCaptor = ArgumentCaptor.forClass(char[].class);

        teacherService.changePassword(EMAIL, PASSWORD, NEW_PASS);

        verify(teacherRepository).findByEmail(EMAIL);
        verify(teacherRepository, times(2)).findPasswordById(ID);
        verify(passwordEncoder).matches(CharBuffer.wrap(PASSWORD), teacherRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by teacher's id")));
        verify(teacherRepository).changePasswordById(Arrays.toString(newPasswordCaptor.capture()),eq(teacher.getId()));

    }

    @Test
    public void teacherService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchTeacherPassword() {
        when(passwordEncoder.matches(CharBuffer.wrap(PASSWORD), teacherRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by teacher's id")))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> teacherService.changePassword(EMAIL, PASSWORD, NEW_PASS));
    }
}

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
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.services.PasswordService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import java.nio.CharBuffer;
import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private static final Teacher teacher = new Teacher(ID, "Bob", "First", EMAIL, "password", "TEACHER");


    @Autowired
    private TeacherService teacherService;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private PasswordService passwordService;

    @BeforeEach
    public void setUp() {
        when(teacherRepository.findByEmail(EMAIL)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findPasswordById(teacher.getId())).thenReturn(Optional.of(Arrays.toString(PASSWORD)));
    }

    @Test
    public void teacherService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        char[] oldPassword = "oldPassword".toCharArray();
        char[] newPassword = "newPassword".toCharArray();

        Teacher teacher = new Teacher(ID, "Alex", "First", EMAIL, "encodedOldPassword", "TEACHER");

        when(teacherRepository.findByEmail(EMAIL)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findPasswordById(ID)).thenReturn(Optional.of("encodedOldPassword"));
        when(passwordEncoder.matches(any(CharSequence.class), eq("encodedOldPassword"))).thenReturn(true);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedNewPassword");

        teacherService.changePassword(EMAIL, oldPassword, newPassword);

        verify(teacherRepository).findByEmail(EMAIL);
        verify(teacherRepository).findPasswordById(ID);
        verify(passwordEncoder).matches(any(CharSequence.class), eq("encodedOldPassword"));
        verify(passwordEncoder).encode(any(CharSequence.class));
        verify(teacherRepository).changePasswordById("encodedNewPassword", ID);
    }

    @Test
    public void teacherService_shouldThrowAnException_whenInputOldPasswordDoesNotMatchTeacherPassword() {
        when(passwordEncoder.matches(CharBuffer.wrap(PASSWORD), teacherRepository.findPasswordById(ID).orElseThrow(() -> new IllegalArgumentException("Password was not found by teacher's id")))).thenReturn(false);
        Assertions.assertThrows(InvalidOldPasswordException.class, () -> teacherService.changePassword(EMAIL, PASSWORD, NEW_PASS));
    }

    @Test
    public void teacherService_shouldCreateTeacherAccount_whenInputIsValid() {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setPassword("encoded-generated-password");
            return null;
        }).when(passwordService).generateAndSendPasswordForUser(any(User.class));

        when(teacherRepository.save(any(Teacher.class))).thenReturn(new Teacher(
                2,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                "encoded-generated-password",
                "TEACHER"
        ));

        Teacher actual = teacherService.createTeacherAccount(
                "Alice",
                "Brown",
                "alice.brown@example.com"
        );

        assertEquals(2, actual.getId());
        assertEquals("Alice", actual.getFirstName());
        assertEquals("Brown", actual.getLastName());
        assertEquals("alice.brown@example.com", actual.getEmail());
        assertEquals("encoded-generated-password", actual.getPassword());
        assertEquals("TEACHER", actual.getRole());

        verify(passwordService).generateAndSendPasswordForUser(any(User.class));
        verify(teacherRepository).save(argThat(teacher ->
                teacher.getId() == null
                        && "Alice".equals(teacher.getFirstName())
                        && "Brown".equals(teacher.getLastName())
                        && "alice.brown@example.com".equals(teacher.getEmail())
                        && "encoded-generated-password".equals(teacher.getPassword())
                        && "TEACHER".equals(teacher.getRole())
        ));
    }
}

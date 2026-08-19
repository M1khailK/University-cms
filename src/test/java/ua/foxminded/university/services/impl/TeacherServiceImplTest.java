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
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.services.PasswordService;
import ua.foxminded.university.services.TeacherService;

import java.nio.CharBuffer;
import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
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
        when(teacherRepository.findByEmailAndRole(EMAIL, "TEACHER")).thenReturn(Optional.of(teacher));
        when(teacherRepository.findPasswordById(teacher.getId())).thenReturn(Optional.of(Arrays.toString(PASSWORD)));
    }

    @Test
    public void teacherService_shouldChangePassword_whenInputHasOldPasswordNewPasswordAndEmail() {
        char[] oldPassword = "oldPassword".toCharArray();
        char[] newPassword = "newPassword".toCharArray();

        Teacher teacher = new Teacher(ID, "Alex", "First", EMAIL, "encodedOldPassword", "TEACHER");

        when(teacherRepository.findByEmailAndRole(EMAIL, "TEACHER")).thenReturn(Optional.of(teacher));
        when(teacherRepository.findPasswordById(ID)).thenReturn(Optional.of("encodedOldPassword"));
        when(passwordEncoder.matches(any(CharSequence.class), eq("encodedOldPassword"))).thenReturn(true);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedNewPassword");

        teacherService.changePassword(EMAIL, oldPassword, newPassword);

        verify(teacherRepository).findByEmailAndRole(EMAIL, "TEACHER");
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

    @Test
    public void teacherService_shouldUpdateTeacherProfile_whenInputIsValid() {
        Teacher existingTeacher = new Teacher(
                1,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "encoded-password",
                "TEACHER"
        );

        Teacher updatedTeacher = new Teacher(
                1,
                "Robert",
                "Johnson",
                "robert.johnson@example.com",
                "encoded-password",
                "TEACHER"
        );

        when(teacherRepository.findByIdAndRole(1, "TEACHER")).thenReturn(Optional.of(existingTeacher));
        when(teacherRepository.save(existingTeacher)).thenReturn(updatedTeacher);

        Teacher actual = teacherService.updateTeacherProfile(
                1,
                "Robert",
                "Johnson",
                "robert.johnson@example.com"
        );

        assertEquals(1, actual.getId());
        assertEquals("Robert", actual.getFirstName());
        assertEquals("Johnson", actual.getLastName());
        assertEquals("robert.johnson@example.com", actual.getEmail());
        assertEquals("encoded-password", actual.getPassword());
        assertEquals("TEACHER", actual.getRole());

        verify(teacherRepository).findByIdAndRole(1, "TEACHER");
        verify(teacherRepository).save(argThat(teacher ->
                teacher.getId().equals(1)
                        && "Robert".equals(teacher.getFirstName())
                        && "Johnson".equals(teacher.getLastName())
                        && "robert.johnson@example.com".equals(teacher.getEmail())
                        && "encoded-password".equals(teacher.getPassword())
                        && "TEACHER".equals(teacher.getRole())
        ));
    }

    @Test
    public void teacherService_shouldReturnOnlyTeachers_whenGetAllIsCalled() {
        Teacher teacher = new Teacher(
                ID,
                "Bob",
                "First",
                EMAIL,
                "password",
                "TEACHER"
        );

        when(teacherRepository.findAllByRole("TEACHER")).thenReturn(List.of(teacher));

        List<Teacher> actual = teacherService.getAll();

        assertEquals(List.of(teacher), actual);
        verify(teacherRepository).findAllByRole("TEACHER");
    }

    @Test
    public void teacherService_shouldCreateAdminAccount_whenInputIsValid() {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setPassword("encoded-generated-password");
            return null;
        }).when(passwordService).generateAndSendPasswordForUser(any(User.class));

        when(teacherRepository.save(any(Teacher.class))).thenReturn(new Teacher(
                100,
                "Alice",
                "Root",
                "alice.root@example.com",
                "encoded-generated-password",
                "ADMIN"
        ));

        Teacher actual = teacherService.createAdminAccount(
                "Alice",
                "Root",
                "alice.root@example.com"
        );

        assertEquals(100, actual.getId());
        assertEquals("Alice", actual.getFirstName());
        assertEquals("Root", actual.getLastName());
        assertEquals("alice.root@example.com", actual.getEmail());
        assertEquals("encoded-generated-password", actual.getPassword());
        assertEquals("ADMIN", actual.getRole());

        verify(passwordService).generateAndSendPasswordForUser(any(User.class));
        verify(teacherRepository).save(argThat(admin ->
                admin.getId() == null
                        && "Alice".equals(admin.getFirstName())
                        && "Root".equals(admin.getLastName())
                        && "alice.root@example.com".equals(admin.getEmail())
                        && "encoded-generated-password".equals(admin.getPassword())
                        && "ADMIN".equals(admin.getRole())
        ));
    }
}

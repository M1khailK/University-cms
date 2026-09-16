package ua.foxminded.university.services.assistant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversityAssistantToolsTest {

    @Mock
    private ServiceManager serviceManager;
    @Mock
    private UserService userService;
    @Mock
    private UserManagerService userManagerService;

    @Test
    void getMyProfile_shouldUseTrustedContext_whenUserIsStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        Student student = new Student(
                1,
                "Alice",
                "Brown",
                "student@university.com",
                group,
                "encoded-password",
                "STUDENT"
        );

        when(serviceManager.getServiceByRole("ROLE_STUDENT"))
                .thenReturn(Optional.of(userManagerService));

        when(userManagerService.getByEmail("student@university.com"))
                .thenReturn(student);

        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(
                Map.of(
                        UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                        new AssistantToolContext(
                                "student@university.com",
                                "ROLE_STUDENT"
                        )
                )
        );

        UniversityAssistantTools.ProfileResult result =
                tools.getMyProfile(toolContext);

        assertEquals("Alice", result.firstName());
        assertEquals("Brown", result.lastName());
        assertEquals("student@university.com", result.email());
        assertEquals("STUDENT", result.role());
        assertEquals("AA-11", result.groupName());

        verify(serviceManager).getServiceByRole("ROLE_STUDENT");
        verify(userManagerService)
                .getByEmail("student@university.com");
    }

    @Test
    void getMyProfile_shouldReturnTeacherProfile_whenUserIsTeacher() {
        Teacher teacher = new Teacher(
                2,
                "Bob",
                "Smith",
                "teacher@university.com",
                "encoded-password",
                "TEACHER"
        );

        when(serviceManager.getServiceByRole("ROLE_TEACHER"))
                .thenReturn(Optional.of(userManagerService));

        when(userManagerService.getByEmail("teacher@university.com"))
                .thenReturn(teacher);

        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(
                Map.of(
                        UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                        new AssistantToolContext(
                                "teacher@university.com",
                                "ROLE_TEACHER"
                        )
                )
        );

        UniversityAssistantTools.ProfileResult result =
                tools.getMyProfile(toolContext);

        assertEquals("Bob", result.firstName());
        assertEquals("Smith", result.lastName());
        assertEquals("teacher@university.com", result.email());
        assertEquals("TEACHER", result.role());
        assertEquals(null, result.groupName());
    }

    @Test
    void getMyProfile_shouldRejectCall_whenAuthenticatedContextIsMissing() {
        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(Map.of());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tools.getMyProfile(toolContext)
        );

        assertEquals(
                "Authenticated assistant context is missing.",
                exception.getMessage()
        );
    }

    @Test
    void getMyProfile_shouldRejectCall_whenRoleHasNoProfileService() {
        when(serviceManager.getServiceByRole("ROLE_ADMIN"))
                .thenReturn(Optional.empty());

        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(
                Map.of(
                        UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                        new AssistantToolContext(
                                "admin@university.com",
                                "ROLE_ADMIN"
                        )
                )
        );

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> tools.getMyProfile(toolContext)
        );

        assertEquals(
                "Profile service is not available for the authenticated role.",
                exception.getMessage()
        );
    }

    @Test
    void getMySchedule_shouldUseTrustedIdentityAndRequestedDateRange() {
        LocalDate from = LocalDate.of(2026, 9, 17);
        LocalDate to = LocalDate.of(2026, 9, 18);

        Subject subject = new Subject();
        subject.setName("Algorithms");

        Group group = new Group();
        group.setName("AA-11");

        Teacher teacher = new Teacher();
        teacher.setFirstName("Bob");
        teacher.setLastName("Smith");

        Lesson lesson = new Lesson();
        lesson.setName("Algorithms lecture");
        lesson.setDate(LocalDate.of(2026, 9, 17));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(11, 30));
        lesson.setSubject(subject);
        lesson.setGroup(group);
        lesson.setTeacher(teacher);

        when(userService.getUserLessons(
                "student@university.com",
                "ROLE_STUDENT",
                from,
                to
        )).thenReturn(List.of(lesson));

        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(
                Map.of(
                        UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                        new AssistantToolContext(
                                "student@university.com",
                                "ROLE_STUDENT"
                        )
                )
        );

        UniversityAssistantTools.ScheduleResult result =
                tools.getMySchedule(
                        "2026-09-17",
                        "2026-09-18",
                        toolContext
                );

        assertTrue(result.successful());
        assertEquals(1, result.lessons().size());

        UniversityAssistantTools.ScheduleLessonResult lessonResult =
                result.lessons().getFirst();

        assertEquals("Algorithms lecture", lessonResult.name());
        assertEquals(LocalDate.of(2026, 9, 17), lessonResult.date());
        assertEquals(LocalTime.of(10, 0), lessonResult.startTime());
        assertEquals(LocalTime.of(11, 30), lessonResult.endTime());
        assertEquals("Algorithms", lessonResult.subjectName());
        assertEquals("AA-11", lessonResult.groupName());
        assertEquals("Bob Smith", lessonResult.teacherName());

        verify(userService).getUserLessons(
                "student@university.com",
                "ROLE_STUDENT",
                from,
                to
        );
    }

    @Test
    void getMySchedule_shouldReturnInvalidRequest_whenDateRangeIsTooLarge() {
        UniversityAssistantTools tools =
                new UniversityAssistantTools(
                        serviceManager,
                        userService
                );

        ToolContext toolContext = new ToolContext(
                Map.of(
                        UniversityAssistantTools.ASSISTANT_CONTEXT_KEY,
                        new AssistantToolContext(
                                "student@university.com",
                                "ROLE_STUDENT"
                        )
                )
        );

        UniversityAssistantTools.ScheduleResult result =
                tools.getMySchedule(
                        "2026-09-01",
                        "2026-10-03",
                        toolContext
                );

        assertFalse(result.successful());
        assertEquals(
                "Schedule date range must not exceed 31 days.",
                result.message()
        );
        assertEquals(List.of(), result.lessons());

        verifyNoInteractions(userService);
    }
}
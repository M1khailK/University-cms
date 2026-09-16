package ua.foxminded.university.services.assistant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversityAssistantToolsTest {

    @Mock
    private ServiceManager serviceManager;

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
                new UniversityAssistantTools(serviceManager);

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
                new UniversityAssistantTools(serviceManager);

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
                new UniversityAssistantTools(serviceManager);

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
                new UniversityAssistantTools(serviceManager);

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
}
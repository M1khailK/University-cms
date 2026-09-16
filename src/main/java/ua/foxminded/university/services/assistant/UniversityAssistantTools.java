package ua.foxminded.university.services.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

@Component
@RequiredArgsConstructor
public class UniversityAssistantTools {

    public static final String ASSISTANT_CONTEXT_KEY = "assistantContext";

    private final ServiceManager serviceManager;

    @Tool(
            description = """
                    Get profile information for the currently authenticated
                    student or teacher. Use this tool only when the user asks
                    about their own profile.
                    """
    )
    public ProfileResult getMyProfile(ToolContext toolContext) {
        Object contextValue = toolContext
                .getContext()
                .get(ASSISTANT_CONTEXT_KEY);

        if (!(contextValue instanceof AssistantToolContext context)) {
            throw new IllegalStateException(
                    "Authenticated assistant context is missing."
            );
        }

        UserManagerService userManagerService = serviceManager
                .getServiceByRole(context.role())
                .orElseThrow(() -> new IllegalStateException(
                        "Profile service is not available for the authenticated role."
                ));

        Object user = userManagerService.getByEmail(context.email());

        if (user instanceof Student student) {
            Group group = student.getGroup();

            return new ProfileResult(
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getRole(),
                    group == null ? null : group.getName()
            );
        }

        if (user instanceof Teacher teacher) {
            return new ProfileResult(
                    teacher.getFirstName(),
                    teacher.getLastName(),
                    teacher.getEmail(),
                    teacher.getRole(),
                    null
            );
        }

        throw new IllegalStateException(
                "Unsupported profile type: " + user.getClass().getName()
        );
    }

    public record ProfileResult(
            String firstName,
            String lastName,
            String email,
            String role,
            String groupName
    ) {
    }
}
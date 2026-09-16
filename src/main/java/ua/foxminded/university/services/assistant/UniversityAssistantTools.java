package ua.foxminded.university.services.assistant;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UniversityAssistantTools {

    public static final String ASSISTANT_CONTEXT_KEY = "assistantContext";

    private final ServiceManager serviceManager;
    private final UserService userService;

    @Tool(
            description = """
                    Get profile information for the currently authenticated
                    student or teacher. Use this tool only when the user asks
                    about their own profile.
                    """
    )
    public ProfileResult getMyProfile(ToolContext toolContext) {
        AssistantToolContext context = getAssistantContext(toolContext);

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

    @Tool(
            description = """
                    Get the schedule for the currently authenticated student or teacher.
                    Both dates must use ISO format YYYY-MM-DD.
                    The requested date range must not exceed 31 days.
                    Use this tool only for the current user's own schedule.
                    If the user asks for all lessons without a bounded period,
                    do not request more than 31 days.
                    """
    )
    public ScheduleResult getMySchedule(
            @ToolParam(
                    description = "Start date in ISO format YYYY-MM-DD"
            )
            String from,

            @ToolParam(
                    description = "End date in ISO format YYYY-MM-DD"
            )
            String to,

            ToolContext toolContext
    ) {
        AssistantToolContext context =
                getAssistantContext(toolContext);

        if (from == null || from.isBlank()
                || to == null || to.isBlank()) {
            return ScheduleResult.invalidRequest(
                    "Both from and to dates are required."
            );
        }

        LocalDate fromDate;
        LocalDate toDate;

        try {
            fromDate = LocalDate.parse(from);
            toDate = LocalDate.parse(to);
        } catch (DateTimeParseException exception) {
            return ScheduleResult.invalidRequest(
                    "Dates must use format YYYY-MM-DD."
            );
        }

        if (toDate.isBefore(fromDate)) {
            return ScheduleResult.invalidRequest(
                    "Schedule end date cannot be before start date."
            );
        }

        if (fromDate.plusDays(31).isBefore(toDate)) {
            return ScheduleResult.invalidRequest(
                    "Schedule date range must not exceed 31 days."
            );
        }

        List<ScheduleLessonResult> lessons =
                userService
                        .getUserLessons(
                                context.email(),
                                context.role(),
                                fromDate,
                                toDate
                        )
                        .stream()
                        .map(this::toScheduleLessonResult)
                        .toList();

        return ScheduleResult.success(lessons);
    }

    private AssistantToolContext getAssistantContext(
            ToolContext toolContext
    ) {
        Object contextValue = toolContext
                .getContext()
                .get(ASSISTANT_CONTEXT_KEY);

        if (!(contextValue instanceof AssistantToolContext context)) {
            throw new IllegalStateException(
                    "Authenticated assistant context is missing."
            );
        }

        return context;
    }

    private LocalDate parseDate(String value, String parameterName) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Invalid " + parameterName
                            + " date. Expected format YYYY-MM-DD.",
                    exception
            );
        }
    }

    private ScheduleLessonResult toScheduleLessonResult(Lesson lesson) {
        return new ScheduleLessonResult(
                lesson.getName(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getSubject() == null
                        ? null
                        : lesson.getSubject().getName(),
                lesson.getGroup() == null
                        ? null
                        : lesson.getGroup().getName(),
                lesson.getTeacher() == null
                        ? null
                        : lesson.getTeacher().getFirstName()
                        + " "
                        + lesson.getTeacher().getLastName()
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

    public record ScheduleLessonResult(
            String name,
            LocalDate date,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            String subjectName,
            String groupName,
            String teacherName
    ) {
    }

    public record ScheduleResult(
            boolean successful,
            String message,
            List<ScheduleLessonResult> lessons
    ) {

        public static ScheduleResult success(
                List<ScheduleLessonResult> lessons
        ) {
            return new ScheduleResult(
                    true,
                    lessons.isEmpty()
                            ? "No lessons found in the requested date range."
                            : null,
                    lessons
            );
        }

        public static ScheduleResult invalidRequest(String message) {
            return new ScheduleResult(
                    false,
                    message,
                    List.of()
            );
        }
    }
}
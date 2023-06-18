package ua.foxminded.university.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.AccountCreatorService;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import javax.sql.DataSource;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(DataSource.class)
@MockBean(ServiceManager.class)
@MockBean(EmailSenderService.class)
@MockBean(PasswordGenerator.class)
@MockBean(AccountCreatorService.class)
@Import(SecurityConfig.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private TeacherService teacherService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private UserService userService;
    @MockBean
    private SubjectService subjectService;

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void generalScheduleController_shouldShowGeneralSchedulePage_whenUserHasRoleOrIsAnonymousAndInputIsEmpty(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generalSchedule").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void studentController_shouldShowStudentSchedule_whenUserHasRoleOrIsAnonymousAndInputHasStudentIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void teacherController_shouldShowTeacherScheduleForAnyUser_whenUserHasRoleOrIsAnonymousAndInputHasTeacherIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void groupScheduleController_shouldThrowAnException_whenDateFromIsNull(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }
    @ParameterizedTest
    @MethodSource("provideRoles")
    public void teacherScheduleController_shouldThrowAnException_whenDateFromIsNull(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("username").roles("STUDENT"),
                user("username").roles("TEACHER"),
                user("username").roles("ADMIN"),
                anonymous());
    }


}

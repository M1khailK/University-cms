package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(LessonService.class)
@MockBean(GroupService.class)
@MockBean(UserService.class)
@MockBean(SubjectService.class)
public class UserScheduleControllerTest {
    private static final int ID = 1;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServiceManager serviceManager;
    @MockBean
    private UserService userService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        LocalDate localDateFrom = LocalDate.of(2023, 1, 1);
        LocalDate localDateTo = LocalDate.of(2023, 1, 30);
        Subject subject = new Subject(1, "Math");
        Teacher teacher = new Teacher(1, "Viktoria", "Second", "teacher@gmail.com");
        Group group = new Group(1, "AA-10");
        Lesson lesson = new Lesson(1, "Lesson of mathematics",
                LocalDate.of(2023, 1, 15), null, null, subject,
                group, teacher);

        doNothing().when(serviceManager).register("[ROLE_STUDENT]", studentService);
        doNothing().when(serviceManager).register("[ROLE_TEACHER]", teacherService);

        when(serviceManager.getServiceByRole("[ROLE_STUDENT]")).thenReturn(Optional.of(studentService));
        when(serviceManager.getServiceByRole("[ROLE_TEACHER]")).thenReturn(Optional.of(teacherService));

        when(userService.getUserIdByEmail("student@gmail.com")).thenReturn(ID);
        when(userService.getUserIdByEmail("teacher@gmail.com")).thenReturn(ID);

        when(serviceManager.getServiceByRole("[ROLE_STUDENT]").get().getLessonsByUserIdAndDateBetween(ID, localDateFrom, localDateTo)).thenReturn(List.of(lesson));
        when(serviceManager.getServiceByRole("[ROLE_TEACHER]").get().getLessonsByUserIdAndDateBetween(ID, localDateFrom, localDateTo)).thenReturn(List.of(lesson));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    void userScheduleController_shouldShowUserSchedulePage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/mySchedule").with(user))
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("userSchedule"));
    }
    @ParameterizedTest
    @MethodSource("provideRoles")
    void userScheduleController_shouldShowUserSchedule_whenInputHasDateFromAndDateTo(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserSchedule").with(user)
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.view().name("userSchedule"));
    }
    @ParameterizedTest
    @MethodSource("provideRoles")
    public void userScheduleController_shouldThrowAnException_whenDateFromIsNull(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserSchedule").with(user)
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }
    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("username").roles("STUDENT"),
                user("username").roles("TEACHER"));
    }
}

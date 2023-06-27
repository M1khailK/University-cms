package ua.foxminded.university.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
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

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(ServiceManager.class)
@MockBean(TeacherService.class)
@MockBean(StudentService.class)
@ContextConfiguration(classes = ControllersTestConfig.class)
public class UserScheduleControllerTest {
    private static final int ID = 1;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


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
        when(userService.getUserLessons(null, LocalDate.of(2023, 1, 30))).thenThrow(InvalidDateRangeException.class);
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

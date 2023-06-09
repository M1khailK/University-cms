package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import ua.foxminded.university.customexceptions.InvalidUserIdException;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import javax.sql.DataSource;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(DataSource.class)
@MockBean(EmailSenderService.class)
@MockBean(LessonService.class)
@MockBean(GroupService.class)
@MockBean(SubjectService.class)
@MockBean(ServiceManager.class)
@Import(SecurityConfig.class)
public class UserDeactivationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        doThrow(InvalidUserIdException.class).when(userService).disableUserById(null);
    }

    @Test
    public void userDeactivationController_shouldShowDeactivationPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivationPage").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("deactivationPage"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @Test
    public void userDeactivationController_shouldDeactivateUser_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivateUser").with(user("admin").roles("ADMIN"))
                .param("userId", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/deactivationPage"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void userDeactivationController_shouldNotShowDeactivationPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivationPage").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void userDeactivationController_shouldNotDeactivateUser_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivateUser").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void userDeactivationController_shouldThrowException_whenInputUserIdIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivateUser").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));

    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("studentName").roles("STUDENT"),
                user("teacherName").roles("TEACHER"));
    }
}

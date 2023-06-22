package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.dto.User;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(PasswordGenerator.class)
@MockBean(AccountCreatorService.class)
@MockBean(StudentService.class)
@MockBean(TeacherService.class)
@MockBean(GroupService.class)
@MockBean(SubjectService.class)
@MockBean(LessonService.class)
@MockBean(EmailSenderService.class)
@MockBean(PasswordEncoder.class)
@MockBean(ServiceManager.class)
@MockBean(UserService.class)
@MockBean(DataSource.class)
@Import(SecurityConfig.class)
public class AccountCreatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void accountCreatorController_shouldShowAccountCreatorPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createAccount").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountCreator"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void accountCreatorController_shouldNotCreateStudentAccount_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createAccount").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void accountCreatorController_shouldCreateStudentAccount_whenInputIsUserObject() throws Exception {
        User user = new User();
        user.setEmail("userEmail@email.com");
        user.setGroupName("userGroup");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        mockMvc.perform(MockMvcRequestBuilders.post("/createStudent").with(user("admin").roles("ADMIN")).with(csrf()).flashAttr("user", user))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }
    @Test
    public void accountCreatorController_shouldCreateTeacherAccount_whenInputIsUserObject() throws Exception {
        User user = new User();
        user.setEmail("userEmail@email.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        mockMvc.perform(MockMvcRequestBuilders.post("/createTeacher").with(user("admin").roles("ADMIN")).with(csrf()).flashAttr("user", user))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }
    @Test
    public void accountCreatorController_shouldCreateAdminAccount_whenInputIsUserObject() throws Exception {
        User user = new User();
        user.setEmail("userEmail@email.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        mockMvc.perform(MockMvcRequestBuilders.post("/createAdmin").with(user("admin").roles("ADMIN")).with(csrf()).flashAttr("user", user))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void accountCreatorController_shouldNotShowAccountCreatorPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createAccount").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("studentName").roles("STUDENT"),
                user("teacherName").roles("TEACHER"));
    }
}

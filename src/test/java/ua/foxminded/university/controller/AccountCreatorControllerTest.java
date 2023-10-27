package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(UserService.class)
@ContextConfiguration(classes = ControllersTestConfig.class)
public class AccountCreatorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        when(serviceManager.getServiceByRole("ROLE_STUDENT")).thenReturn(Optional.of(studentService));
        when(serviceManager.getServiceByRole("ROLE_TEACHER")).thenReturn(Optional.of(teacherService));
        when(serviceManager.getServiceByRole("ROLE_ADMIN")).thenReturn(Optional.of(teacherService));
    }

    @Test
    public void accountCreatorController_shouldShowAccountCreatorPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createAccount").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("accountCreator"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
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
        user.setRole("STUDENT");

        doNothing().when(studentService).createUserAccountByRole(user, user.getRole());

        mockMvc.perform(MockMvcRequestBuilders.post("/createUserAccount")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .flashAttr("user", user)
                .param("role", user.getRole()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(serviceManager.getServiceByRole("ROLE_STUDENT").get(),times(1))
                .createUserAccountByRole(user,"STUDENT");
    }

    @Test
    public void accountCreatorController_shouldCreateTeacherAccount_whenInputIsUserObject() throws Exception {
        User user = new User();
        user.setEmail("userEmail@email.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        user.setRole("TEACHER");

        doNothing().when(teacherService).createUserAccountByRole(user, user.getRole());

        mockMvc.perform(MockMvcRequestBuilders.post("/createUserAccount").with(user("admin").roles("ADMIN"))
                .with(csrf())
                .flashAttr("user", user)
                .param("role", user.getRole()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(serviceManager.getServiceByRole("ROLE_TEACHER").get(),times(1))
                .createUserAccountByRole(user,"TEACHER");

    }

    @Test
    public void accountCreatorController_shouldCreateAdminAccount_whenInputIsUserObject() throws Exception {
        User user = new User();
        user.setEmail("userEmail@email.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        user.setRole("ADMIN");

        doNothing().when(teacherService).createUserAccountByRole(user, user.getRole());

        mockMvc.perform(MockMvcRequestBuilders.post("/createUserAccount").with(user("admin").roles("ADMIN"))
                .with(csrf())
                .flashAttr("user", user)
                .param("role", user.getRole()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createAccount"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(serviceManager.getServiceByRole("ROLE_ADMIN").get(),times(1))
                .createUserAccountByRole(user,"ADMIN");
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void accountCreatorController_shouldNotShowAccountCreatorPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createAccount").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}

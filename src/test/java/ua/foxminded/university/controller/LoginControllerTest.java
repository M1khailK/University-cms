package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.AccountCreatorService;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import javax.sql.DataSource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(PasswordGenerator.class)
@MockBean(AccountCreatorService.class)
@MockBean(EmailSenderService.class)
@MockBean(TeacherService.class)
@MockBean(StudentService.class)
@MockBean(GroupService.class)
@MockBean(LessonService.class)
@MockBean(UserService.class)
@MockBean(DataSource.class)
@MockBean(ServiceManager.class)
@Import(SecurityConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    public void loginController_shouldShowLoginPage_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN","STUDENT","TEACHER"})
    public void loginController_shouldShowAnErrorMessage_whenUserIsLoggedIn(String role) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login").with(user("username").roles(role)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


}

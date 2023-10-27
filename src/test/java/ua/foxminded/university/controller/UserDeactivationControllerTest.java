package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.UserService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class UserDeactivationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

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

        verify(userService,times(1)).disableUserById(1);
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void userDeactivationController_shouldNotShowDeactivationPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivationPage").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void userDeactivationController_shouldNotDeactivateUser_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deactivateUser").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}

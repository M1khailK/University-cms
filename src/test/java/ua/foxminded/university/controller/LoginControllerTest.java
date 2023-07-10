package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest

@ContextConfiguration(classes = ControllersTestConfig.class)
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

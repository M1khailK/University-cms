package ua.foxminded.university.controller;

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

@WebMvcTest(controllers = HomePageController.class)
@ContextConfiguration(classes = ControllersTestConfig.class)
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource(RoleProvider.ALL_ROLES)
    public void homepageController_shouldShowHomePage_whenUserHasRoleOrIsAnonymous(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"));
    }

}

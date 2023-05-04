package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(controllers = HomePageController.class)
@MockBean(ScheduleController.class)
@MockBean(TeacherService.class)
@MockBean(StudentService.class)
@MockBean(GroupService.class)
@MockBean(LessonService.class)
@MockBean(DataSource.class)
@Import(SecurityConfig.class)
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void homepageController_shouldShowHomePage_whenUserHasRoleOrIsAnonymous(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"));
    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("username").roles("STUDENT"),
                user("username").roles("TEACHER"),
                user("username").roles("ADMIN"),
                anonymous());
    }

}

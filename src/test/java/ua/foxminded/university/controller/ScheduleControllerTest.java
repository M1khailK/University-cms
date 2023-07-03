package ua.foxminded.university.controller;

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
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

@WebMvcTest
@MockBean(ServiceManager.class)
@MockBean(TeacherService.class)
@MockBean(StudentService.class)
@MockBean(UserService.class)
@ContextConfiguration(classes = ControllersTestConfig.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("ua.foxminded.university.roleProvider.RoleProvider#provideAllRoles")
    public void generalScheduleController_shouldShowGeneralSchedulePage_whenUserHasRoleOrIsAnonymousAndInputIsEmpty(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generalSchedule").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @ParameterizedTest
    @MethodSource("ua.foxminded.university.roleProvider.RoleProvider#provideAllRoles")
    public void studentController_shouldShowStudentSchedule_whenUserHasRoleOrIsAnonymousAndInputHasStudentIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    @ParameterizedTest
    @MethodSource("ua.foxminded.university.roleProvider.RoleProvider#provideAllRoles")
    public void teacherController_shouldShowTeacherScheduleForAnyUser_whenUserHasRoleOrIsAnonymousAndInputHasTeacherIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    @ParameterizedTest
    @MethodSource("ua.foxminded.university.roleProvider.RoleProvider#provideAllRoles")
    public void groupScheduleController_shouldThrowAnException_whenDateFromIsInvalid(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }
    @ParameterizedTest
    @MethodSource("ua.foxminded.university.roleProvider.RoleProvider#provideAllRoles")
    public void teacherScheduleController_shouldThrowAnException_whenDateFromIsnvalid(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }

}

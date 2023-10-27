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
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.LessonService;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LessonService lessonService;

    @ParameterizedTest
    @MethodSource(RoleProvider.ALL_ROLES)
    public void generalScheduleController_shouldShowGeneralSchedulePage_whenUserHasRoleOrIsAnonymousAndInputIsEmpty(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generalSchedule").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.ALL_ROLES)
    public void studentController_shouldShowStudentSchedule_whenUserHasRoleOrIsAnonymousAndInputHasStudentIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
        verify(lessonService).getAllByGroupAndDateBetween(null, LocalDate.of(2023,1,1),LocalDate.of(2023,1,30));

    }

    @ParameterizedTest
    @MethodSource(RoleProvider.ALL_ROLES)
    public void teacherController_shouldShowTeacherScheduleForAnyUser_whenUserHasRoleOrIsAnonymousAndInputHasTeacherIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));

        verify(lessonService).getAllByTeacherAndDateBetween(null, LocalDate.of(2023,1,1),LocalDate.of(2023,1,30));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.ALL_ROLES)
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
    @MethodSource(RoleProvider.ALL_ROLES)
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

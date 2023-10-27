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
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class UserScheduleControllerTest {
    private static final int ID = 1;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;


    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    void userScheduleController_shouldShowUserSchedulePage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/mySchedule").with(user))
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("userSchedule"));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    void userScheduleController_shouldShowUserSchedule_whenInputHasDateFromAndDateTo(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserSchedule").with(user)
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30")).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.view().name("userSchedule"));

        verify(userService).getUserLessons(LocalDate.of(2023,1,1),LocalDate.of(2023,1,30));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void userScheduleController_shouldThrowAnException_whenDateFromIsNull(RequestPostProcessor user) throws Exception {
        when(userService.getUserLessons(null, LocalDate.of(2023, 1, 30))).thenThrow(InvalidDateRangeException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/getUserSchedule").with(user)
                .param("dateFrom", "")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.view().name("errorPage"));
    }

}

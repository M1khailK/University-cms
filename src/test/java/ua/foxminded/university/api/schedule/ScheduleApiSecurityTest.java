package ua.foxminded.university.api.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.schedule.mapper.ScheduleMapperImpl;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ScheduleRestController.class)
@Import({SecurityConfig.class, ScheduleMapperImpl.class, ApiExceptionHandler.class})
class ScheduleApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void scheduleApiSecurity_shouldAllowAnonymousToReadGroupSchedule() throws Exception {
        Group group = new Group(20, "AA-01", Collections.emptyList());

        when(groupService.getById(20)).thenReturn(group);
        when(lessonService.getAllByGroupAndDateBetween(group, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/schedules/groups/{groupId}", 20))
                .andExpect(status().isOk());
    }

    @Test
    void scheduleApiSecurity_shouldAllowAnonymousToReadTeacherSchedule() throws Exception {
        Teacher teacher = new Teacher(
                30,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );

        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.getAllByTeacherAndDateBetween(teacher, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/schedules/teachers/{teacherId}", 30))
                .andExpect(status().isOk());
    }

    @Test
    void scheduleApiSecurity_shouldAllowStudentToReadGroupSchedule() throws Exception {
        Group group = new Group(20, "AA-01", Collections.emptyList());

        when(groupService.getById(20)).thenReturn(group);
        when(lessonService.getAllByGroupAndDateBetween(group, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/schedules/groups/{groupId}", 20)
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void scheduleApiSecurity_shouldAllowTeacherToReadTeacherSchedule() throws Exception {
        Teacher teacher = new Teacher(
                30,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );

        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.getAllByTeacherAndDateBetween(teacher, null, null))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/schedules/teachers/{teacherId}", 30)
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());
    }
}
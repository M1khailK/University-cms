package ua.foxminded.university.api.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.schedule.mapper.ScheduleMapperImpl;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ScheduleRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ScheduleMapperImpl.class, ApiExceptionHandler.class})
class ScheduleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private TeacherService teacherService;

    @Test
    void scheduleRestController_shouldReturnGroupSchedule_whenRequestIsValid() throws Exception {
        Group group = new Group(20, "AA-01", Collections.emptyList());
        Lesson lesson = createLesson();

        when(groupService.getById(20)).thenReturn(group);
        when(lessonService.getAllByGroupAndDateBetween(
                group,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 30)
        )).thenReturn(List.of(lesson));

        mockMvc.perform(get("/api/v1/schedules/groups/{groupId}", 20)
                        .param("from", "2023-01-01")
                        .param("to", "2023-01-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Basics"))
                .andExpect(jsonPath("$[0].date").value("2023-08-05"))
                .andExpect(jsonPath("$[0].startTime").value("15:30:00"))
                .andExpect(jsonPath("$[0].endTime").value("16:30:00"))
                .andExpect(jsonPath("$[0].subjectId").value(10))
                .andExpect(jsonPath("$[0].subjectName").value("Java"))
                .andExpect(jsonPath("$[0].groupId").value(20))
                .andExpect(jsonPath("$[0].groupName").value("AA-01"))
                .andExpect(jsonPath("$[0].teacherId").value(30))
                .andExpect(jsonPath("$[0].teacherFirstName").value("Bob"))
                .andExpect(jsonPath("$[0].teacherLastName").value("Smith"))
                .andExpect(jsonPath("$[0].teacherEmail").doesNotExist());

        verify(groupService).getById(20);
        verify(lessonService).getAllByGroupAndDateBetween(
                group,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 30)
        );
    }

    @Test
    void scheduleRestController_shouldReturnTeacherSchedule_whenRequestIsValid() throws Exception {
        Teacher teacher = new Teacher(
                30,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );
        Lesson lesson = createLesson();

        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.getAllByTeacherAndDateBetween(
                teacher,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 30)
        )).thenReturn(List.of(lesson));

        mockMvc.perform(get("/api/v1/schedules/teachers/{teacherId}", 30)
                        .param("from", "2023-01-01")
                        .param("to", "2023-01-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Basics"))
                .andExpect(jsonPath("$[0].date").value("2023-08-05"))
                .andExpect(jsonPath("$[0].startTime").value("15:30:00"))
                .andExpect(jsonPath("$[0].endTime").value("16:30:00"))
                .andExpect(jsonPath("$[0].subjectId").value(10))
                .andExpect(jsonPath("$[0].subjectName").value("Java"))
                .andExpect(jsonPath("$[0].groupId").value(20))
                .andExpect(jsonPath("$[0].groupName").value("AA-01"))
                .andExpect(jsonPath("$[0].teacherId").value(30))
                .andExpect(jsonPath("$[0].teacherFirstName").value("Bob"))
                .andExpect(jsonPath("$[0].teacherLastName").value("Smith"))
                .andExpect(jsonPath("$[0].teacherEmail").doesNotExist());

        verify(teacherService).getById(30);
        verify(lessonService).getAllByTeacherAndDateBetween(
                teacher,
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 30)
        );
    }

    @Test
    void scheduleRestController_shouldReturnBadRequest_whenGroupScheduleToDateProvidedWithoutFromDate() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/groups/{groupId}", 20)
                        .param("to", "2023-01-30"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid date range"))
                .andExpect(jsonPath("$.detail").value("From date cannot be null when To date is provided."));
    }

    @Test
    void scheduleRestController_shouldReturnBadRequest_whenTeacherScheduleToDateProvidedWithoutFromDate() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/teachers/{teacherId}", 30)
                        .param("to", "2023-01-30"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid date range"))
                .andExpect(jsonPath("$.detail").value("From date cannot be null when To date is provided."));
    }

    private Lesson createLesson() {
        Subject subject = new Subject(10, "Java");
        Group group = new Group(20, "AA-01", Collections.emptyList());
        Teacher teacher = new Teacher(
                30,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );

        return new Lesson(
                1,
                "Java Basics",
                LocalDate.parse("2023-08-05"),
                LocalTime.parse("15:30"),
                LocalTime.parse("16:30"),
                subject,
                group,
                teacher
        );
    }

    @Test
    void scheduleRestController_shouldReturnScheduleOptions_whenRequestIsValid() throws Exception {
        Group group = new Group(20, "AA-01", Collections.emptyList());
        Teacher teacher = new Teacher(
                30,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );

        when(groupService.getAll()).thenReturn(List.of(group));
        when(teacherService.getAll()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/api/v1/schedules/options"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groups[0].id").value(20))
                .andExpect(jsonPath("$.groups[0].name").value("AA-01"))
                .andExpect(jsonPath("$.teachers[0].id").value(30))
                .andExpect(jsonPath("$.teachers[0].firstName").value("Bob"))
                .andExpect(jsonPath("$.teachers[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.teachers[0].email").doesNotExist());

        verify(groupService).getAll();
        verify(teacherService).getAll();
    }
}
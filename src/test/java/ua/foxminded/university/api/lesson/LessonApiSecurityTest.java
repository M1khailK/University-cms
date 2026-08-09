package ua.foxminded.university.api.lesson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.lesson.mapper.LessonApiMapperImpl;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LessonRestController.class)
@Import({SecurityConfig.class, LessonApiMapperImpl.class, ApiExceptionHandler.class})
class LessonApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private TeacherService teacherService;

    @Test
    void shouldAllowAdminToReadAllLessons() throws Exception {
        when(lessonService.getAll()).thenReturn(List.of(createLesson()));

        mockMvc.perform(get("/api/v1/lessons")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToReadLessonById() throws Exception {
        when(lessonService.getById(1)).thenReturn(createLesson());

        mockMvc.perform(get("/api/v1/lessons/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidStudentToReadAllLessons() throws Exception {
        mockMvc.perform(get("/api/v1/lessons")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidTeacherToReadAllLessons() throws Exception {
        mockMvc.perform(get("/api/v1/lessons")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRedirectAnonymousUserToLoginWhenReadingLessons() throws Exception {
        mockMvc.perform(get("/api/v1/lessons"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
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
    void shouldAllowAdminToCreateLesson() throws Exception {
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

        when(subjectService.getById(10)).thenReturn(subject);
        when(groupService.getById(20)).thenReturn(group);
        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.create(any(Lesson.class))).thenReturn(createLesson());

        mockMvc.perform(post("/api/v1/lessons")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Java Basics",
                                  "date": "2023-08-05",
                                  "startTime": "15:30:00",
                                  "endTime": "16:30:00",
                                  "subjectId": 10,
                                  "groupId": 20,
                                  "teacherId": 30
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldForbidStudentToCreateLesson() throws Exception {
        mockMvc.perform(post("/api/v1/lessons")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Java Basics",
                                  "date": "2023-08-05",
                                  "startTime": "15:30:00",
                                  "endTime": "16:30:00",
                                  "subjectId": 10,
                                  "groupId": 20,
                                  "teacherId": 30
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
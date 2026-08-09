package ua.foxminded.university.api.lesson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.lesson.mapper.LessonApiMapperImpl;
import ua.foxminded.university.customexceptions.LessonNotFoundException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LessonRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({LessonApiMapperImpl.class, ApiExceptionHandler.class})
class LessonRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private TeacherService teacherService;

    @Test
    void shouldReturnAllLessons() throws Exception {
        Lesson lesson = createLesson();

        when(lessonService.getAll()).thenReturn(List.of(lesson));

        mockMvc.perform(get("/api/v1/lessons"))
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
                .andExpect(jsonPath("$[0].teacherEmail").value("bob.smith@example.com"))
                .andExpect(jsonPath("$[0].teacherPassword").doesNotExist())
                .andExpect(jsonPath("$[0].teacherRole").doesNotExist());
    }

    @Test
    void shouldReturnLessonById() throws Exception {
        Lesson lesson = createLesson();

        when(lessonService.getById(1)).thenReturn(lesson);

        mockMvc.perform(get("/api/v1/lessons/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Basics"))
                .andExpect(jsonPath("$.date").value("2023-08-05"))
                .andExpect(jsonPath("$.startTime").value("15:30:00"))
                .andExpect(jsonPath("$.endTime").value("16:30:00"))
                .andExpect(jsonPath("$.subjectId").value(10))
                .andExpect(jsonPath("$.subjectName").value("Java"))
                .andExpect(jsonPath("$.groupId").value(20))
                .andExpect(jsonPath("$.groupName").value("AA-01"))
                .andExpect(jsonPath("$.teacherId").value(30))
                .andExpect(jsonPath("$.teacherFirstName").value("Bob"))
                .andExpect(jsonPath("$.teacherLastName").value("Smith"))
                .andExpect(jsonPath("$.teacherEmail").value("bob.smith@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenLessonDoesNotExist() throws Exception {
        when(lessonService.getById(999)).thenThrow(new LessonNotFoundException(999));

        mockMvc.perform(get("/api/v1/lessons/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Lesson not found"))
                .andExpect(jsonPath("$.detail").value("Lesson was not found by id: 999"));
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
    void shouldCreateLesson() throws Exception {
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

        Lesson createdLesson = createLesson();

        when(subjectService.getById(10)).thenReturn(subject);
        when(groupService.getById(20)).thenReturn(group);
        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.create(any(Lesson.class))).thenReturn(createdLesson);

        mockMvc.perform(post("/api/v1/lessons")
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
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/lessons/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Basics"))
                .andExpect(jsonPath("$.subjectId").value(10))
                .andExpect(jsonPath("$.groupId").value(20))
                .andExpect(jsonPath("$.teacherId").value(30));

        verify(subjectService).getById(10);
        verify(groupService).getById(20);
        verify(teacherService).getById(30);
        verify(lessonService).create(any(Lesson.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingLessonWithBlankName() throws Exception {
        mockMvc.perform(post("/api/v1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "date": "2023-08-05",
                                  "startTime": "15:30:00",
                                  "endTime": "16:30:00",
                                  "subjectId": 10,
                                  "groupId": 20,
                                  "teacherId": 30
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEndTimeIsBeforeStartTime() throws Exception {
        mockMvc.perform(post("/api/v1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Java Basics",
                                  "date": "2023-08-05",
                                  "startTime": "16:30:00",
                                  "endTime": "15:30:00",
                                  "subjectId": 10,
                                  "groupId": 20,
                                  "teacherId": 30
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateLesson() throws Exception {
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

        Lesson updatedLesson = createLesson();

        when(subjectService.getById(10)).thenReturn(subject);
        when(groupService.getById(20)).thenReturn(group);
        when(teacherService.getById(30)).thenReturn(teacher);
        when(lessonService.update(any(Integer.class), any(Lesson.class))).thenReturn(updatedLesson);

        mockMvc.perform(put("/api/v1/lessons/{id}", 1)
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Basics"))
                .andExpect(jsonPath("$.subjectId").value(10))
                .andExpect(jsonPath("$.groupId").value(20))
                .andExpect(jsonPath("$.teacherId").value(30));

        verify(subjectService).getById(10);
        verify(groupService).getById(20);
        verify(teacherService).getById(30);
        verify(lessonService).update(any(Integer.class), any(Lesson.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingLesson() throws Exception {
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
        when(lessonService.update(any(Integer.class), any(Lesson.class)))
                .thenThrow(new LessonNotFoundException(999));

        mockMvc.perform(put("/api/v1/lessons/{id}", 999)
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
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Lesson not found"))
                .andExpect(jsonPath("$.detail").value("Lesson was not found by id: 999"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingLessonWithBlankName() throws Exception {
        mockMvc.perform(put("/api/v1/lessons/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "date": "2023-08-05",
                                  "startTime": "15:30:00",
                                  "endTime": "16:30:00",
                                  "subjectId": 10,
                                  "groupId": 20,
                                  "teacherId": 30
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
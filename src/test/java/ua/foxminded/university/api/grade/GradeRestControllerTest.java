package ua.foxminded.university.api.grade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.grade.mapper.GradeMapper;
import ua.foxminded.university.customexceptions.GradeAccessDeniedException;
import ua.foxminded.university.customexceptions.GradeNotFoundException;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GradeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GradeRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GradeMapper.class, ApiExceptionHandler.class})
class GradeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradeService gradeService;

    @Test
    void gradeRestController_shouldReturnAllGrades_whenRequestedByPrivilegedUser() throws Exception {
        when(gradeService.getAllGrades()).thenReturn(List.of(createGrade()));

        mockMvc.perform(get("/api/v1/grades"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(500))
                .andExpect(jsonPath("$[0].value").value(5))
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].studentFirstName").value("Alice"))
                .andExpect(jsonPath("$[0].studentLastName").value("Brown"))
                .andExpect(jsonPath("$[0].studentEmail").value("alice.brown@example.com"))
                .andExpect(jsonPath("$[0].lessonId").value(100))
                .andExpect(jsonPath("$[0].lessonName").value("Math lesson"))
                .andExpect(jsonPath("$[0].password").doesNotExist());

        verify(gradeService).getAllGrades();
    }

    @Test
    void gradeRestController_shouldReturnCurrentStudentGrades_whenCurrentUserIsStudent() throws Exception {
        when(gradeService.getGradesByEmail("alice.brown@example.com")).thenReturn(List.of(createGrade()));

        mockMvc.perform(get("/api/v1/grades/me")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "alice.brown@example.com",
                                "password"
                        )))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(500))
                .andExpect(jsonPath("$[0].value").value(5))
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].studentFirstName").value("Alice"))
                .andExpect(jsonPath("$[0].studentLastName").value("Brown"))
                .andExpect(jsonPath("$[0].studentEmail").value("alice.brown@example.com"))
                .andExpect(jsonPath("$[0].lessonId").value(100))
                .andExpect(jsonPath("$[0].lessonName").value("Math lesson"))
                .andExpect(jsonPath("$[0].password").doesNotExist());

        verify(gradeService).getGradesByEmail("alice.brown@example.com");
    }

    private Grade createGrade() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        Student student = new Student(
                1,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                group,
                "encoded-password",
                "STUDENT"
        );

        Lesson lesson = new Lesson();
        lesson.setId(100);
        lesson.setName("Math lesson");
        lesson.setDate(LocalDate.of(2023, 4, 27));
        lesson.setStartTime(LocalTime.of(10, 0));
        lesson.setEndTime(LocalTime.of(12, 0));

        return new Grade(
                500,
                student,
                lesson,
                5
        );
    }

    @Test
    void gradeRestController_shouldCreateGrade_whenRequestIsValid() throws Exception {
        when(gradeService.createGrade(
                eq(1),
                eq(100),
                eq(5),
                eq("teacher@example.com")
        )).thenReturn(createGrade());

        mockMvc.perform(post("/api/v1/grades")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 1,
                                  "lessonId": 100,
                                  "value": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(500))
                .andExpect(jsonPath("$.value").value(5))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.lessonId").value(100))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(gradeService).createGrade(1, 100, 5, "teacher@example.com");
    }

    @Test
    void gradeRestController_shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/grades")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 1,
                                  "lessonId": 100,
                                  "value": 6
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gradeRestController_shouldReturnForbidden_whenTeacherDoesNotOwnLesson() throws Exception {
        when(gradeService.createGrade(
                eq(1),
                eq(100),
                eq(5),
                eq("teacher@example.com")
        )).thenThrow(new GradeAccessDeniedException(100));

        mockMvc.perform(post("/api/v1/grades")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": 1,
                                  "lessonId": 100,
                                  "value": 5
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Grade access denied"));
    }

    @Test
    void gradeRestController_shouldDeleteGrade_whenGradeExistsAndTeacherOwnsLesson() throws Exception {
        mockMvc.perform(delete("/api/v1/grades/500")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        )))
                .andExpect(status().isNoContent());

        verify(gradeService).deleteGrade(500, "teacher@example.com");
    }

    @Test
    void gradeRestController_shouldReturnNotFound_whenDeletedGradeDoesNotExist() throws Exception {
        doThrow(new GradeNotFoundException(500))
                .when(gradeService)
                .deleteGrade(500, "teacher@example.com");

        mockMvc.perform(delete("/api/v1/grades/500")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        )))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Grade not found"));
    }
}
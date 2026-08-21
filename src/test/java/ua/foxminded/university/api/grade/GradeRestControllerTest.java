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
import ua.foxminded.university.api.grade.mapper.GradeMapper;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GradeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GradeRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GradeMapper.class)
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
}
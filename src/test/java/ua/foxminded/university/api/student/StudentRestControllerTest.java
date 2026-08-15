package ua.foxminded.university.api.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.student.mapper.StudentMapperImpl;
import ua.foxminded.university.customexceptions.StudentNotFoundException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.StudentService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({StudentMapperImpl.class, ApiExceptionHandler.class})
class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void studentRestController_shouldReturnAllStudents_whenStudentsExist() throws Exception {
        Student student = createStudent();

        when(studentService.getAll()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].lastName").value("Brown"))
                .andExpect(jsonPath("$[0].email").value("alice.brown@example.com"))
                .andExpect(jsonPath("$[0].groupId").value(10))
                .andExpect(jsonPath("$[0].groupName").value("AA-11"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].role").doesNotExist());
    }

    @Test
    void studentRestController_shouldReturnStudentById_whenStudentExists() throws Exception {
        Student student = createStudent();

        when(studentService.getById(1)).thenReturn(student);

        mockMvc.perform(get("/api/v1/students/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("alice.brown@example.com"))
                .andExpect(jsonPath("$.groupId").value(10))
                .andExpect(jsonPath("$.groupName").value("AA-11"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void studentRestController_shouldReturnNotFound_whenStudentDoesNotExist() throws Exception {
        when(studentService.getById(999)).thenThrow(new StudentNotFoundException(999));

        mockMvc.perform(get("/api/v1/students/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Student not found"))
                .andExpect(jsonPath("$.detail").value("Student was not found by id: 999"));
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "alice.brown@example.com",
                group,
                "secret-password",
                "STUDENT"
        );
    }
}
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
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.StudentNotFoundException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @MockitoBean
    private UserService userService;

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

    @Test
    void studentRestController_shouldCreateStudent_whenRequestIsValid() throws Exception {
        Student createdStudent = createStudent();

        when(studentService.createStudentAccount(
                eq("Alice"),
                eq("Brown"),
                eq("alice.brown@example.com"),
                eq(10)
        )).thenReturn(createdStudent);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com",
                                  "groupId": 10
                                }
                                """))
                .andExpect(status().isCreated())
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
    void studentRestController_shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Brown",
                                  "email": "invalid-email",
                                  "groupId": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentRestController_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        when(studentService.createStudentAccount(
                eq("Alice"),
                eq("Brown"),
                eq("alice.brown@example.com"),
                eq(10)
        )).thenThrow(new DuplicateEmailException("Email already exists. Please choose a different email."));

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "email": "alice.brown@example.com",
                                  "groupId": 10
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Duplicate email"))
                .andExpect(jsonPath("$.detail").value("Email already exists. Please choose a different email."));
    }

    @Test
    void studentRestController_shouldUpdateStudent_whenRequestIsValid() throws Exception {
        Group group = new Group();
        group.setId(20);
        group.setName("BB-22");

        Student updatedStudent = new Student(
                1,
                "Alicia",
                "Johnson",
                "alicia.johnson@example.com",
                group,
                "encoded-password",
                "STUDENT"
        );

        when(studentService.updateStudentProfile(
                eq(1),
                eq("Alicia"),
                eq("Johnson"),
                eq("alicia.johnson@example.com"),
                eq(20)
        )).thenReturn(updatedStudent);

        mockMvc.perform(put("/api/v1/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alicia",
                                  "lastName": "Johnson",
                                  "email": "alicia.johnson@example.com",
                                  "groupId": 20
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email").value("alicia.johnson@example.com"))
                .andExpect(jsonPath("$.groupId").value(20))
                .andExpect(jsonPath("$.groupName").value("BB-22"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void studentRestController_shouldReturnBadRequest_whenUpdateRequestIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Johnson",
                                  "email": "invalid-email",
                                  "groupId": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentRestController_shouldReturnNotFound_whenUpdatedStudentDoesNotExist() throws Exception {
        when(studentService.updateStudentProfile(
                eq(999),
                eq("Alicia"),
                eq("Johnson"),
                eq("alicia.johnson@example.com"),
                eq(20)
        )).thenThrow(new StudentNotFoundException(999));

        mockMvc.perform(put("/api/v1/students/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alicia",
                                  "lastName": "Johnson",
                                  "email": "alicia.johnson@example.com",
                                  "groupId": 20
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Student not found"))
                .andExpect(jsonPath("$.detail").value("Student was not found by id: 999"));
    }

    @Test
    void studentRestController_shouldReturnConflict_whenUpdatedEmailAlreadyExists() throws Exception {
        when(studentService.updateStudentProfile(
                eq(1),
                eq("Alicia"),
                eq("Johnson"),
                eq("existing@example.com"),
                eq(20)
        )).thenThrow(new DuplicateEmailException("Email already exists. Please choose a different email."));

        mockMvc.perform(put("/api/v1/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alicia",
                                  "lastName": "Johnson",
                                  "email": "existing@example.com",
                                  "groupId": 20
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Duplicate email"))
                .andExpect(jsonPath("$.detail").value("Email already exists. Please choose a different email."));
    }

    @Test
    void studentRestController_shouldDeactivateStudent_whenStudentExists() throws Exception {
        Student student = createStudent();

        when(studentService.getById(1)).thenReturn(student);

        mockMvc.perform(delete("/api/v1/students/{id}", 1))
                .andExpect(status().isNoContent());

        verify(studentService).getById(1);
        verify(userService).disableUserById(1);
    }

    @Test
    void studentRestController_shouldReturnNotFound_whenDeactivatedStudentDoesNotExist() throws Exception {
        when(studentService.getById(999)).thenThrow(new StudentNotFoundException(999));

        mockMvc.perform(delete("/api/v1/students/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Student not found"))
                .andExpect(jsonPath("$.detail").value("Student was not found by id: 999"));

        verify(studentService).getById(999);
        verify(userService, never()).disableUserById(anyInt());
    }
}
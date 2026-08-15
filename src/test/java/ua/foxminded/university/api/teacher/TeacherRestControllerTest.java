package ua.foxminded.university.api.teacher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.teacher.mapper.TeacherMapperImpl;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.customexceptions.TeacherNotFoundException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;
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

@WebMvcTest(controllers = TeacherRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TeacherMapperImpl.class, ApiExceptionHandler.class})
class TeacherRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private UserService userService;

    @Test
    void teacherRestController_shouldReturnAllTeachers_whenTeachersExist() throws Exception {
        Teacher teacher = createTeacher();

        when(teacherService.getAll()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/api/v1/teachers"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1)).andExpect(jsonPath("$[0].firstName")
                        .value("Bob")).andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].email").value("bob.smith@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist()).andExpect(jsonPath("$[0].role").doesNotExist());
    }

    @Test
    void teacherRestController_shouldReturnTeacherById_whenTeacherExists() throws Exception {
        Teacher teacher = createTeacher();

        when(teacherService.getById(1)).thenReturn(teacher);

        mockMvc.perform(get("/api/v1/teachers/{id}", 1)).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("bob.smith@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void teacherRestController_shouldReturnNotFound_whenTeacherDoesNotExist() throws Exception {
        when(teacherService.getById(999)).thenThrow(new TeacherNotFoundException(999));

        mockMvc.perform(get("/api/v1/teachers/{id}", 999))
                .andExpect(status().isNotFound()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Teacher not found"))
                .andExpect(jsonPath("$.detail").value("Teacher was not found by id: 999"));
    }

    private Teacher createTeacher() {
        return new Teacher(1, "Bob", "Smith", "bob.smith@example.com", "secret-password", "TEACHER");
    }

    @Test
    void teacherRestController_shouldCreateTeacher_whenRequestIsValid() throws Exception {
        Teacher createdTeacher = new Teacher(2, "Alice", "Brown", "alice.brown@example.com", "encoded-password", "TEACHER");

        when(teacherService.createTeacherAccount(eq("Alice"), eq("Brown"), eq("alice.brown@example.com"))).thenReturn(createdTeacher);

        mockMvc.perform(post("/api/v1/teachers").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "firstName": "Alice",
                          "lastName": "Brown",
                          "email": "alice.brown@example.com"
                        }
                        """)).andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("alice.brown@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void teacherRestController_shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/teachers").contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "firstName": "",
                  "lastName": "Brown",
                  "email": "invalid-email"
                }
                """)).andExpect(status().isBadRequest());
    }

    @Test
    void teacherRestController_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        when(teacherService.createTeacherAccount(eq("Alice"), eq("Brown"), eq("alice.brown@example.com"))).thenThrow(new DuplicateEmailException("Email already exists. Please choose a different email."));

        mockMvc.perform(post("/api/v1/teachers").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "firstName": "Alice",
                          "lastName": "Brown",
                          "email": "alice.brown@example.com"
                        }
                        """)).andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Duplicate email"))
                .andExpect(jsonPath("$.detail").value("Email already exists. Please choose a different email."));
    }

    @Test
    void teacherRestController_shouldUpdateTeacher_whenRequestIsValid() throws Exception {
        Teacher updatedTeacher = new Teacher(1, "Robert", "Johnson", "robert.johnson@example.com", "encoded-password", "TEACHER");

        when(teacherService.updateTeacherProfile(eq(1), eq("Robert"), eq("Johnson"), eq("robert.johnson@example.com"))).thenReturn(updatedTeacher);

        mockMvc.perform(put("/api/v1/teachers/{id}", 1).contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "firstName": "Robert",
                          "lastName": "Johnson",
                          "email": "robert.johnson@example.com"
                        }
                        """)).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Robert"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.email").value("robert.johnson@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void teacherRestController_shouldReturnBadRequest_whenUpdateRequestIsInvalid() throws Exception {
        mockMvc.perform(put("/api/v1/teachers/{id}", 1).contentType(MediaType.APPLICATION_JSON).content("""
                {
                  "firstName": "",
                  "lastName": "Johnson",
                  "email": "invalid-email"
                }
                """)).andExpect(status().isBadRequest());
    }

    @Test
    void teacherRestController_shouldReturnNotFound_whenUpdatedTeacherDoesNotExist() throws Exception {
        when(teacherService.updateTeacherProfile(eq(999), eq("Robert"), eq("Johnson"), eq("robert.johnson@example.com"))).thenThrow(new TeacherNotFoundException(999));

        mockMvc.perform(put("/api/v1/teachers/{id}", 999).contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "firstName": "Robert",
                          "lastName": "Johnson",
                          "email": "robert.johnson@example.com"
                        }
                        """)).andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Teacher not found"))
                .andExpect(jsonPath("$.detail").value("Teacher was not found by id: 999"));
    }

    @Test
    void teacherRestController_shouldReturnConflict_whenUpdatedEmailAlreadyExists() throws Exception {
        when(teacherService.updateTeacherProfile(eq(1), eq("Robert"), eq("Johnson"), eq("existing@example.com"))).thenThrow(new DuplicateEmailException("Email already exists. Please choose a different email."));

        mockMvc.perform(put("/api/v1/teachers/{id}", 1).contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "firstName": "Robert",
                          "lastName": "Johnson",
                          "email": "existing@example.com"
                        }
                        """)).andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Duplicate email"))
                .andExpect(jsonPath("$.detail").value("Email already exists. Please choose a different email."));
    }

    @Test
    void teacherRestController_shouldDeactivateTeacher_whenTeacherExists() throws Exception {
        Teacher teacher = createTeacher();

        when(teacherService.getById(1)).thenReturn(teacher);

        mockMvc.perform(delete("/api/v1/teachers/{id}", 1))
                .andExpect(status().isNoContent());

        verify(teacherService).getById(1);
        verify(userService).disableUserById(1);
    }

    @Test
    void teacherRestController_shouldReturnNotFound_whenDeactivatedTeacherDoesNotExist() throws Exception {
        when(teacherService.getById(999)).thenThrow(new TeacherNotFoundException(999));

        mockMvc.perform(delete("/api/v1/teachers/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Teacher not found"))
                .andExpect(jsonPath("$.detail").value("Teacher was not found by id: 999"));

        verify(teacherService).getById(999);
        verify(userService, never()).disableUserById(anyInt());
    }
}
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
import ua.foxminded.university.customexceptions.TeacherNotFoundException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void teacherRestController_shouldReturnAllTeachers_whenTeachersExist() throws Exception {
        Teacher teacher = createTeacher();

        when(teacherService.getAll()).thenReturn(List.of(teacher));

        mockMvc.perform(get("/api/v1/teachers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Bob"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].email").value("bob.smith@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].role").doesNotExist());
    }

    @Test
    void teacherRestController_shouldReturnTeacherById_whenTeacherExists() throws Exception {
        Teacher teacher = createTeacher();

        when(teacherService.getById(1)).thenReturn(teacher);

        mockMvc.perform(get("/api/v1/teachers/{id}", 1))
                .andExpect(status().isOk())
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
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Teacher not found"))
                .andExpect(jsonPath("$.detail").value("Teacher was not found by id: 999"));
    }

    private Teacher createTeacher() {
        return new Teacher(
                1,
                "Bob",
                "Smith",
                "bob.smith@example.com",
                "secret-password",
                "TEACHER"
        );
    }
}
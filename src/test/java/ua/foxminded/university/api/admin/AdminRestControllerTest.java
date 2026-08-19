package ua.foxminded.university.api.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.admin.mapper.AdminMapperImpl;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.TeacherService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({AdminMapperImpl.class, ApiExceptionHandler.class})
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @Test
    void adminRestController_shouldCreateAdmin_whenRequestIsValid() throws Exception {
        Teacher createdAdmin = createAdmin();

        when(teacherService.createAdminAccount(
                eq("Alice"),
                eq("Root"),
                eq("alice.root@example.com")
        )).thenReturn(createdAdmin);

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Root"))
                .andExpect(jsonPath("$.email").value("alice.root@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void adminRestController_shouldReturnBadRequest_whenCreateRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Root",
                                  "email": "invalid-email"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminRestController_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        when(teacherService.createAdminAccount(
                eq("Alice"),
                eq("Root"),
                eq("alice.root@example.com")
        )).thenThrow(new DuplicateEmailException("Email already exists. Please choose a different email."));

        mockMvc.perform(post("/api/v1/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Root",
                                  "email": "alice.root@example.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Duplicate email"))
                .andExpect(jsonPath("$.detail").value("Email already exists. Please choose a different email."));
    }

    private Teacher createAdmin() {
        return new Teacher(
                100,
                "Alice",
                "Root",
                "alice.root@example.com",
                "encoded-password",
                "ADMIN"
        );
    }
}
package ua.foxminded.university.api.subject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.subject.mapper.SubjectMapperImpl;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.services.SubjectService;

import javax.sql.DataSource;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubjectRestController.class)
@Import({
        SecurityConfig.class,
        SubjectMapperImpl.class,
        ApiExceptionHandler.class
})
class SubjectApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void shouldAllowPublicReadSubjects() throws Exception {
        when(subjectService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/subjects"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToCreateSubject() throws Exception {
        when(subjectService.create(any(Subject.class)))
                .thenReturn(new Subject(1, "Biology"));

        mockMvc.perform(post("/api/v1/subjects")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Biology"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldForbidStudentToCreateSubject() throws Exception {
        mockMvc.perform(post("/api/v1/subjects")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Biology"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToUpdateSubject() throws Exception {
        when(subjectService.updateName(1, "Advanced Biology"))
                .thenReturn(new Subject(1, "Advanced Biology"));

        mockMvc.perform(put("/api/v1/subjects/{id}", 1)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Advanced Biology"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidStudentToUpdateSubject() throws Exception {
        mockMvc.perform(put("/api/v1/subjects/{id}", 1)
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Advanced Biology"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteSubject() throws Exception {
        doNothing().when(subjectService).deleteById(1);

        mockMvc.perform(delete("/api/v1/subjects/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldForbidStudentToDeleteSubject() throws Exception {
        mockMvc.perform(delete("/api/v1/subjects/{id}", 1)
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }
}
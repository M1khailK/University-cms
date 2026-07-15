package ua.foxminded.university.api.subject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.subject.mapper.SubjectMapperImpl;
import ua.foxminded.university.customexceptions.SubjectNotFoundException;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.services.SubjectService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubjectRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        SubjectMapperImpl.class,
        ApiExceptionHandler.class
})
class SubjectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubjectService subjectService;

    @Test
    void shouldReturnAllSubjects() throws Exception {
        Subject mathematics = new Subject(1, "Mathematics");
        Subject physics = new Subject(2, "Physics");

        when(subjectService.getAll()).thenReturn(List.of(mathematics, physics));

        mockMvc.perform(get("/api/v1/subjects"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Mathematics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Physics"));
    }

    @Test
    void shouldReturnSubjectById() throws Exception {
        Subject subject = new Subject(1, "Mathematics");

        when(subjectService.getById(1)).thenReturn(subject);

        mockMvc.perform(get("/api/v1/subjects/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mathematics"));
    }

    @Test
    void shouldReturnNotFoundWhenSubjectDoesNotExist() throws Exception {
        when(subjectService.getById(999)).thenThrow(new SubjectNotFoundException(999));

        mockMvc.perform(get("/api/v1/subjects/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Subject not found"))
                .andExpect(jsonPath("$.detail").value("Subject was not found by id: 999"));
    }
}
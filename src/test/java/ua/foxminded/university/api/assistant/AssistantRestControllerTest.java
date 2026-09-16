package ua.foxminded.university.api.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.customexceptions.AssistantUnavailableException;
import ua.foxminded.university.services.AssistantService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AssistantRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class AssistantRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssistantService assistantService;

    @Test
    void assistantRestController_shouldReturnAnswer_whenRequestIsValid()
            throws Exception {

        when(assistantService.answer("What can you help me with?"))
                .thenReturn("I can help with University-CMS questions.");

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "What can you help me with?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.answer")
                        .value("I can help with University-CMS questions."));
    }

    @Test
    void assistantRestController_shouldReturnBadRequest_whenMessageIsBlank()
            throws Exception {

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON
                        ))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.message").exists());

        verify(assistantService, never()).answer("   ");
    }

    @Test
    void assistantRestController_shouldReturnBadRequest_whenMessageIsTooLong()
            throws Exception {

        String message = "a".repeat(2001);

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "%s"
                                }
                                """.formatted(message)))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON
                        ))
                .andExpect(jsonPath("$.title")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.message").exists());

        verify(assistantService, never()).answer(message);
    }

    @Test
    void assistantRestController_shouldReturnServiceUnavailable_whenAssistantIsUnavailable()
            throws Exception {

        when(assistantService.answer("Hello"))
                .thenThrow(new AssistantUnavailableException(
                        "AI assistant is not configured."
                ));

        mockMvc.perform(post("/api/v1/assistant/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hello"
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON
                        ))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.title")
                        .value("AI assistant unavailable"))
                .andExpect(jsonPath("$.detail")
                        .value("AI assistant is not configured."));
    }
}
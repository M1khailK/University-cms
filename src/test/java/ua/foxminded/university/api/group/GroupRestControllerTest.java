package ua.foxminded.university.api.group;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.group.mapper.GroupMapperImpl;
import ua.foxminded.university.customexceptions.GroupNotFoundException;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.services.GroupService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GroupRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GroupMapperImpl.class,
        ApiExceptionHandler.class
})
class GroupRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @Test
    void shouldReturnAllGroups() throws Exception {
        Group firstGroup = new Group(1, "AA-01", Collections.emptyList());
        Group secondGroup = new Group(2, "BB-02", Collections.emptyList());

        when(groupService.getAll()).thenReturn(List.of(firstGroup, secondGroup));

        mockMvc.perform(get("/api/v1/groups"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("AA-01"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("BB-02"));
    }

    @Test
    void shouldReturnGroupById() throws Exception {
        Group group = new Group(1, "AA-01", Collections.emptyList());

        when(groupService.getById(1)).thenReturn(group);

        mockMvc.perform(get("/api/v1/groups/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("AA-01"));
    }

    @Test
    void shouldReturnNotFoundWhenGroupDoesNotExist() throws Exception {
        when(groupService.getById(999)).thenThrow(new GroupNotFoundException(999));

        mockMvc.perform(get("/api/v1/groups/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Group not found"))
                .andExpect(jsonPath("$.detail").value("Group was not found by id: 999"));
    }
}
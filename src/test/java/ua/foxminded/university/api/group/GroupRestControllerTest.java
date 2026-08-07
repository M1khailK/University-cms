package ua.foxminded.university.api.group;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

    @Test
    void shouldCreateGroup() throws Exception {
        Group createdGroup = new Group(10, "AA-03", Collections.emptyList());

        when(groupService.create(any(Group.class))).thenReturn(createdGroup);

        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-03"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/groups/10"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("AA-03"));

        ArgumentCaptor<Group> groupCaptor = ArgumentCaptor.forClass(Group.class);
        verify(groupService).create(groupCaptor.capture());

        Group groupToCreate = groupCaptor.getValue();

        assertThat(groupToCreate.getId()).isNull();
        assertThat(groupToCreate.getName()).isEqualTo("AA-03");
        assertThat(groupToCreate.getStudents()).isNull();
    }

    @Test
    void shouldReturnBadRequestWhenGroupNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateGroup() throws Exception {
        Group updatedGroup = new Group(1, "AA-99", Collections.emptyList());

        when(groupService.updateName(1, "AA-99")).thenReturn(updatedGroup);

        mockMvc.perform(put("/api/v1/groups/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-99"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("AA-99"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatedGroupNameIsBlank() throws Exception {
        mockMvc.perform(put("/api/v1/groups/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingGroup() throws Exception {
        when(groupService.updateName(999, "AA-99"))
                .thenThrow(new GroupNotFoundException(999));

        mockMvc.perform(put("/api/v1/groups/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-99"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Group not found"))
                .andExpect(jsonPath("$.detail").value("Group was not found by id: 999"));
    }
}
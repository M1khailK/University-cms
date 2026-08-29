package ua.foxminded.university.api.group;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.common.ApiExceptionHandler;
import ua.foxminded.university.api.group.mapper.GroupMapperImpl;
import ua.foxminded.university.config.JwtConfig;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.services.GroupService;

import javax.sql.DataSource;
import java.util.Collections;
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

@WebMvcTest(controllers = GroupRestController.class)
@Import({
        SecurityConfig.class,
        JwtConfig.class,
        GroupMapperImpl.class,
        ApiExceptionHandler.class
})
class GroupApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void shouldAllowPublicReadGroups() throws Exception {
        when(groupService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/groups"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPublicReadGroupById() throws Exception {
        when(groupService.getById(1))
                .thenReturn(new Group(1, "AA-01", Collections.emptyList()));

        mockMvc.perform(get("/api/v1/groups/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToCreateGroup() throws Exception {
        when(groupService.create(any(Group.class)))
                .thenReturn(new Group(1, "AA-01", Collections.emptyList()));

        mockMvc.perform(post("/api/v1/groups")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-01"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldForbidStudentToCreateGroup() throws Exception {
        mockMvc.perform(post("/api/v1/groups")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-01"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToUpdateGroup() throws Exception {
        when(groupService.updateName(1, "AA-99"))
                .thenReturn(new Group(1, "AA-99", Collections.emptyList()));

        mockMvc.perform(put("/api/v1/groups/{id}", 1)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-99"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidStudentToUpdateGroup() throws Exception {
        mockMvc.perform(put("/api/v1/groups/{id}", 1)
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "AA-99"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToDeleteGroup() throws Exception {
        doNothing().when(groupService).deleteById(1);

        mockMvc.perform(delete("/api/v1/groups/{id}", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldForbidStudentToDeleteGroup() throws Exception {
        mockMvc.perform(delete("/api/v1/groups/{id}", 1)
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReadGroupStudents() throws Exception {
        Group group = new Group(1, "AA-01", Collections.emptyList());

        when(groupService.getById(1)).thenReturn(group);

        mockMvc.perform(get("/api/v1/groups/{id}/students", 1)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidStudentToReadGroupStudents() throws Exception {
        mockMvc.perform(get("/api/v1/groups/{id}/students", 1)
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }
}
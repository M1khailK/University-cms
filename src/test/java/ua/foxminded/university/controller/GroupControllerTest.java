package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.GroupService;

import java.util.Collections;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupService groupService;

    @Test
    public void groupController_shouldShowGroupInfoPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getGroupInfoPage").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("groupInfoPage"))
                .andExpect(MockMvcResultMatchers.model().size(1));

    }

    @Test
    public void groupController_shouldShowGroupInfo_whenUserIsAdmin() throws Exception {
        when(groupService.getById(1)).thenReturn(new Group(1, "name", Collections.emptyList()));
        mockMvc.perform(MockMvcRequestBuilders.get("/getGroupInfo").with(user("admin").roles("ADMIN"))
                .param("group_id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("groupInfoPage"))
                .andExpect(MockMvcResultMatchers.model().size(2));

        verify(groupService,times(1)).getById(1);
    }

    @Test
    public void groupController_shouldChangeGroupName_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/editGroupInfo").with(user("admin").roles("ADMIN"))
                .with(csrf())
                .param("group_id", "1")
                .param("group_name", "newGroupName"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("groupInfoPage"))
                .andExpect(MockMvcResultMatchers.model().size(1));

        verify(groupService,times(1)).changeNameById(1,"newGroupName");
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void groupController_shouldNotShowGroupInfoPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getGroupInfoPage").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void groupController_shouldNotShowGroupInfo_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getGroupInfo").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void groupController_shouldNotEditGroupInfo_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/editGroupInfo").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

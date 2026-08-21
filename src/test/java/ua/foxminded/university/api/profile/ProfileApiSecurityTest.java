package ua.foxminded.university.api.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.profile.mapper.ProfileMapper;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import javax.sql.DataSource;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProfileRestController.class)
@Import({SecurityConfig.class, ProfileMapper.class})
class ProfileApiSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceManager serviceManager;

    @MockitoBean
    private UserManagerService userManagerService;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void profileApiSecurity_shouldAllowReadProfile_whenUserIsStudent() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("student")).thenReturn(createStudent());

        mockMvc.perform(get("/api/v1/profile")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void profileApiSecurity_shouldAllowReadProfile_whenUserIsTeacher() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("teacher")).thenReturn(createTeacher());

        mockMvc.perform(get("/api/v1/profile")
                        .with(user("teacher").roles("TEACHER")))
                .andExpect(status().isOk());
    }

    @Test
    void profileApiSecurity_shouldForbidReadProfile_whenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/profile")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void profileApiSecurity_shouldRedirectToLogin_whenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "student",
                group,
                "encoded-password",
                "STUDENT"
        );
    }

    private Teacher createTeacher() {
        return new Teacher(
                2,
                "Bob",
                "Smith",
                "teacher",
                "encoded-password",
                "TEACHER"
        );
    }

    @Test
    void profileApiSecurity_shouldAllowUpdatePassword_whenUserIsStudent() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);

        mockMvc.perform(put("/api/v1/profile/password")
                        .with(user("student").roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void profileApiSecurity_shouldAllowUpdatePassword_whenUserIsTeacher() throws Exception {
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);

        mockMvc.perform(put("/api/v1/profile/password")
                        .with(user("teacher").roles("TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void profileApiSecurity_shouldForbidUpdatePassword_whenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/v1/profile/password")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void profileApiSecurity_shouldRedirectToLogin_whenAnonymousUpdatesPassword() throws Exception {
        mockMvc.perform(put("/api/v1/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "oldPassword",
                                  "newPassword": "newPassword"
                                }
                                """))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
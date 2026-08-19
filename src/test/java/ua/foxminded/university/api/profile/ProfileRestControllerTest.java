package ua.foxminded.university.api.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.foxminded.university.api.profile.mapper.ProfileMapper;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.UserManagerService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProfileRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProfileMapper.class)
class ProfileRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceManager serviceManager;

    @MockitoBean
    private UserManagerService userManagerService;

    @Test
    void profileRestController_shouldReturnStudentProfile_whenCurrentUserIsStudent() throws Exception {
        Student student = createStudent();

        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("student@example.com")).thenReturn(student);

        mockMvc.perform(get("/api/v1/profile")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "student@example.com",
                                "password"
                        )))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.email").value("student@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.groupId").value(10))
                .andExpect(jsonPath("$.groupName").value("AA-11"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void profileRestController_shouldReturnTeacherProfile_whenCurrentUserIsTeacher() throws Exception {
        Teacher teacher = createTeacher();

        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(userManagerService);
        when(userManagerService.getByEmail("teacher@example.com")).thenReturn(teacher);

        mockMvc.perform(get("/api/v1/profile")
                        .principal(new UsernamePasswordAuthenticationToken(
                                "teacher@example.com",
                                "password"
                        )))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("teacher@example.com"))
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andExpect(jsonPath("$.groupId").doesNotExist())
                .andExpect(jsonPath("$.groupName").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    private Student createStudent() {
        Group group = new Group();
        group.setId(10);
        group.setName("AA-11");

        return new Student(
                1,
                "Alice",
                "Brown",
                "student@example.com",
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
                "teacher@example.com",
                "encoded-password",
                "TEACHER"
        );
    }
}
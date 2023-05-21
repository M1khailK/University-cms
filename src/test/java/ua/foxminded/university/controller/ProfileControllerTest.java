package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(DataSource.class)
@MockBean(LessonService.class)
@MockBean(GroupService.class)
@MockBean(UserService.class)
@MockBean(SubjectService.class)
@Import(SecurityConfig.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ServiceManager serviceManager;
    @MockBean
    private StudentService studentService;
    @MockBean
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        Student student = new Student(1, "Alex", "First", "studentName", null);
        Teacher teacher = new Teacher(1, "Bob", "Second", "teacherName");

        doNothing().when(serviceManager).register("[ROLE_STUDENT]", studentService);
        doNothing().when(serviceManager).register("[ROLE_TEACHER]", teacherService);

        when(serviceManager.getServiceByRole("[ROLE_STUDENT]")).thenReturn(Optional.of(studentService));
        when(serviceManager.getServiceByRole("[ROLE_TEACHER]")).thenReturn(Optional.of(teacherService));

        when(Optional.of(studentService).get().getByEmail(student.getEmail())).thenReturn(student);
        when(Optional.of(teacherService).get().getByEmail(teacher.getEmail())).thenReturn(teacher);


    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    void profileController_shouldShowProfilePage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/profile").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile"))
                .andExpect(MockMvcResultMatchers.model().size(1));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    void profileController_shouldShowSettingsPage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/settings").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profileSettings"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    void profileController_shouldUpdateUserPassword_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/updatePassword").with(user).with(csrf())
                .param("oldPass", "password")
                .param("newPass", "newPassword"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/profile"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("studentName").roles("STUDENT"),
                user("teacherName").roles("TEACHER"));
    }
}

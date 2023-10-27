package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
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
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        Student student = new Student(1, "Alex", "First", "studentName", null,"password","STUDENT");
        Teacher teacher = new Teacher(1, "Bob", "Second", "teacherName","password","TEACHER");

        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(teacherService);
        when(serviceManager.getUserManagerServiceByAuthentication()).thenReturn(studentService);
        when(studentService.getByEmail("username")).thenReturn(student);
        when(teacherService.getByEmail("username")).thenReturn(teacher);
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    void profileController_shouldShowProfilePage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/profile").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile"))
                .andExpect(MockMvcResultMatchers.model().size(1));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    void profileController_shouldShowSettingsPage_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/settings").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profileSettings"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    void profileController_shouldUpdateUserPassword_whenUserIsAuthorized(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/updatePassword").with(user).with(csrf())
                .param("oldPass", "password")
                .param("newPass", "newPassword"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/profile"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(serviceManager.getUserManagerServiceByAuthentication()).changePassword("username", "password".toCharArray(), "newPassword".toCharArray());
    }


}

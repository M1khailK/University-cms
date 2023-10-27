package ua.foxminded.university.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.config.controller.ControllersTestConfig;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.SubjectService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class SubjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SubjectService subjectService;

    @Test
    public void subjectCreatorController_shouldShowSubjectCreatorPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createUniversitySubject").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("subjectCreator"))
                .andExpect(MockMvcResultMatchers.model().size(1));
    }

    @Test
    public void subjectCreatorController_shouldCreateSubject_whenInputIsSubjectObject() throws Exception {
        Subject subject = new Subject(null, "subject");

        mockMvc.perform(MockMvcRequestBuilders.post("/createSubject")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .param("name", subject.getName()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createUniversitySubject"))
                .andExpect(MockMvcResultMatchers.model().size(0));
        Mockito.verify(subjectService, Mockito.times(1)).save(subject);

    }

    @Test
    public void subjectController_shouldChangeSubjectName_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/editSubject").with(user("admin").roles("ADMIN"))
                .with(csrf())
                .param("subject_id", "1")
                .param("subject_name", "newSubjectName"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createUniversitySubject"))
                .andExpect(MockMvcResultMatchers.model().size(0));
        Mockito.verify(subjectService, Mockito.times(1)).changeNameById(1, "newSubjectName");

    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void subjectCreatorController_shouldNotShowSubjectCreatorPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createUniversitySubject").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void subjectCreatorController_shouldNotCreateSubject_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createSubject").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void subjectCreatorController_shouldNotEditSubject_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/editSubject").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

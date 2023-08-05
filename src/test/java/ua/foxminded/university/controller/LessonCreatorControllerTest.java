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
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class LessonCreatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void lessonCreatorController_shouldShowLessonCreatorPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createUniversityLesson").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lessonCreator"))
                .andExpect(MockMvcResultMatchers.model().size(3));
    }

    @Test
    public void lessonCreatorController_shouldCreateLesson_whenInputIsLessonObject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createLesson")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .param("name", "Test Lesson")
                .param("date", "2023-08-05")
                .param("startTime", "15:30")
                .param("endTime", "16:30")
                .param("subjectId", "1")
                .param("groupId", "2")
                .param("teacherId", "3"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createUniversityLesson"))
                .andExpect(MockMvcResultMatchers.model().size(0));
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void lessonCreatorController_shouldNotShowLessonCreatorPage_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createUniversityLesson").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void lessonCreatorController_shouldNotCreateLesson_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/createLesson").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

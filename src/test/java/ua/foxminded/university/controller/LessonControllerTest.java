package ua.foxminded.university.controller;

import org.junit.jupiter.api.Assertions;
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
import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.mapper.LessonMapper;
import ua.foxminded.university.roleProvider.RoleProvider;
import ua.foxminded.university.services.LessonService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@ContextConfiguration(classes = ControllersTestConfig.class)
public class LessonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonMapper lessonMapper;

    @Test
    public void lessonCreatorController_shouldShowLessonCreatorPage_whenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createUniversityLesson").with(user("admin").roles("ADMIN")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("lessonCreator"))
                .andExpect(MockMvcResultMatchers.model().size(4));
    }

    @Test
    public void lessonCreatorController_shouldCreateLesson_whenInputIsLessonObject() throws Exception {
        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setName("Test Lesson");
        lessonDTO.setDate(LocalDate.parse("2023-08-05"));
        lessonDTO.setStartTime(LocalTime.parse("15:30"));
        lessonDTO.setEndTime(LocalTime.parse("16:30"));
        lessonDTO.setSubject(new Subject(1, "subjectName"));
        lessonDTO.setGroup(new Group(2, "groupName", Collections.emptyList()));
        lessonDTO.setTeacher(new Teacher(3, "Bob", "Second", "teacherName", "password", "TEACHER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/createLesson")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .flashAttr("lessonDTO", lessonDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createUniversityLesson"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(lessonMapper, Mockito.times(1)).toLesson(lessonDTO);

        verify(lessonService, Mockito.times(1)).save(lessonMapper.toLesson(lessonDTO));

    }

    @Test
    public void lessonController_shouldEditLessonInfo_whenUserIsAdmin() throws Exception {
        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setName("Test Lesson");
        lessonDTO.setDate(LocalDate.parse("2023-08-05"));
        lessonDTO.setStartTime(LocalTime.parse("15:30"));
        lessonDTO.setEndTime(LocalTime.parse("16:30"));
        lessonDTO.setSubject(new Subject(1, "subjectName"));
        lessonDTO.setGroup(new Group(2, "groupName", Collections.emptyList()));
        lessonDTO.setTeacher(new Teacher(3, "Bob", "Second", "teacherName", "password", "TEACHER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/editLesson")
                .with(user("admin").roles("ADMIN"))
                .flashAttr("lessonDTO", lessonDTO).with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/createUniversityLesson"))
                .andExpect(MockMvcResultMatchers.model().size(0));

        verify(lessonMapper, Mockito.times(1)).toLesson(lessonDTO);

        verify(lessonService, Mockito.times(1)).save(lessonMapper.toLesson(lessonDTO));

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

    @ParameterizedTest
    @MethodSource(RoleProvider.STUDENT_AND_TEACHER_ROLES)
    public void lessonCreatorController_shouldNotEditLesson_whenUserIsNotAdmin(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/editLesson").with(user))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

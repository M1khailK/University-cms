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
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest
@MockBean(DataSource.class)
@MockBean(ServiceManager.class)
@Import(SecurityConfig.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;
    @MockBean
    private LessonService lessonService;
    @MockBean
    private TeacherService teacherService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private SubjectService subjectService;

    @BeforeEach
    public void setUp() {
        LocalDate localDateFrom = LocalDate.of(2023, 1, 1);
        LocalDate localDateTo = LocalDate.of(2023, 1, 30);
        Subject subject = new Subject(1, "Math");
        Teacher teacher = new Teacher(1,"Viktoria", "Second", "foo@gmail.com");
        Group group = new Group(1, "AA-10");
        Student student = new Student(1,"Max", "First", "qwerty@gmail.com", group);

        when(studentService.getAll()).thenReturn(Collections.singletonList(student));
        when(teacherService.getAll()).thenReturn(Collections.singletonList(teacher));

        when(studentService.getById(1)).thenReturn(Optional.of(student));
        when(teacherService.getById(1)).thenReturn(Optional.of(teacher));
        when(groupService.getById(1)).thenReturn(Optional.of(group));

        List<Lesson> singletonList = Collections.singletonList(new Lesson(1, "Lesson of mathematics",
                LocalDate.of(2023, 1, 1), null, null, subject,
                groupService.getById(1).get(), teacherService.getById(1).get()));

       when(lessonService.getAllByStudentAndDateBetween(student, localDateFrom, localDateTo)).
       thenReturn(singletonList);
       when(lessonService.getAllByTeacherAndDateBetween(teacher, localDateFrom, localDateTo))
                .thenReturn(singletonList);
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void generalScheduleController_shouldShowGeneralSchedulePage_whenUserHasRoleOrIsAnonymousAndInputIsEmpty(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generalSchedule").with(user))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void studentController_shouldShowStudentSchedule_whenUserHasRoleOrIsAnonymousAndInputHasStudentIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule").with(user)
                .param("groupId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    public void teacherController_shouldShowTeacherScheduleForAnyUser_whenUserHasRoleOrIsAnonymousAndInputHasTeacherIdAndDateBetween(RequestPostProcessor user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule").with(user)
                .param("teacherId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

    private static Stream<RequestPostProcessor> provideRoles() {
        return Stream.of(
                user("username").roles("STUDENT"),
                user("username").roles("TEACHER"),
                user("username").roles("ADMIN"),
                anonymous());
    }


}

package ua.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.lenient;

@WebMvcTest
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
        Subject subject = new Subject(1,"Math");
        Teacher teacher = new Teacher(1, "Viktoria", "Second", "foo@gmail.com");
        Group group = new Group(1, "AA-10");
        Student student = new Student(1, "Max", "First", "qwerty@gmail.com", group);

        lenient().when(studentService.getAll()).thenReturn(Collections.singletonList(student));
        lenient().when(teacherService.getAll()).thenReturn(Collections.singletonList(teacher));

        lenient().when(studentService.getById(1)).thenReturn(Optional.of(student));
        lenient().when(teacherService.getById(1)).thenReturn(Optional.of(teacher));
        lenient().when(groupService.getById(1)).thenReturn(Optional.of(group));

        List<Lesson> singletonList = Collections.singletonList(new Lesson(1, "Lesson of mathematics",
                LocalDate.of(2023, 1, 1), null, null, subject,
                groupService.getById(1).get(), teacherService.getById(1).get()));

        lenient().when(lessonService.getAllByStudentAndDateBetween(student, localDateFrom, localDateTo))
                .thenReturn(singletonList);
        lenient().when(lessonService.getAllByTeacherAndDateBetween(teacher,localDateFrom,localDateTo))
                .thenReturn(singletonList);
    }
    @Test
    public void generalScheduleController_shouldShowGeneralSchedulePage_whenInputIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/generalSchedule"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"))
                .andExpect(MockMvcResultMatchers.model().size(2));
    }

    @Test
    public void studentController_shouldShowStudentSchedule_whenInputHasStudentIdAndDateBetween() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupSchedule")
                .param("groupId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }
    @Test
    public void teacherController_shouldShowTeacherSchedule_whenInputHasTeacherIdAndDateBetween() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/teacherSchedule")
                .param("teacherId", "1")
                .param("dateFrom", "2023-01-01")
                .param("dateTo", "2023-01-30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.view().name("generalSchedule"));
    }

}

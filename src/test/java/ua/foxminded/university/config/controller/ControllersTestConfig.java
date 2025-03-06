package ua.foxminded.university.config.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.foxminded.university.config.security.SecurityTestConfig;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.mapper.LessonMapper;
import ua.foxminded.university.services.AttendanceService;
import ua.foxminded.university.services.GradeService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

@Configuration
@ComponentScan("ua.foxminded.university.controller")
@Import(SecurityTestConfig.class)
public class ControllersTestConfig {

    @MockBean
    public GroupService groupService;

    @MockBean
    public SubjectService subjectService;

    @MockBean
    public LessonService lessonService;

    @MockBean
    public PasswordEncoder passwordEncoder;
    @MockBean
    public ServiceManager serviceManager;
    @MockBean
    public StudentService studentService;
    @MockBean
    public TeacherService teacherService;
    @MockBean
    public UserService userService;
    @MockBean
    public LessonMapper lessonMapper;
    @MockBean
    public GradeService gradeService;
    @MockBean
    public AttendanceService attendanceService;

}

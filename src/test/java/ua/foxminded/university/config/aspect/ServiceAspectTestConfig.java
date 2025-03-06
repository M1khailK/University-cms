package ua.foxminded.university.config.aspect;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.AttendanceService;
import ua.foxminded.university.services.GradeService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.impl.UserServiceImpl;

import java.time.Clock;

@Configuration
@ComponentScan({"ua.foxminded.university.aop.service", "ua.foxminded.university.services"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceAspectTestConfig {
    @MockBean
    public StudentRepository studentRepository;
    @MockBean
    public LessonRepository lessonRepository;
    @MockBean
    public TeacherRepository teacherRepository;
    @MockBean
    public SubjectRepository subjectRepository;
    @MockBean
    public UserRepository userRepository;
    @MockBean
    public GradeRepository gradeRepository;
    @MockBean
    public AttendanceRepository attendanceRepository;
    @MockBean
    public Clock clock;
    @MockBean
    public PasswordEncoder passwordEncoder;
    @MockBean
    public PasswordGenerator passwordGenerator;
    @MockBean
    public SpringTemplateEngine springTemplateEngine;
    @MockBean
    public JavaMailSender javaMailSender;
    @MockBean
    public TeacherService teacherService;
    @MockBean
    public StudentService studentService;
    @MockBean
    public UserServiceImpl userService;
    @MockBean
    public GradeService gradeService;
    @MockBean
    public AttendanceService attendanceService;

}

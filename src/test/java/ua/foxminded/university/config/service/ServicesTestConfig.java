package ua.foxminded.university.config.service;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.GradeService;

@Configuration
@ComponentScan({"ua.foxminded.university.services","ua.foxminded.university.manager"})
public class ServicesTestConfig {

    @MockBean
    public GroupRepository groupRepository;
    @MockBean
    public SubjectRepository subjectRepository;
    @MockBean
    public PasswordGenerator passwordGenerator;
    @Bean
    public SpringTemplateEngine templateEngine(){
        return new SpringTemplateEngine();
    }
    @Bean
    public JavaMailSenderImpl javaMailSender(){
        return new JavaMailSenderImpl();
    }
    @MockBean
    public UserRepository userRepository;
    @MockBean
    public GradeRepository gradeRepository;
    @MockBean
    public AttendanceRepository attendanceRepository;
}

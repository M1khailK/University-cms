package ua.foxminded.university.config.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.foxminded.university.config.SecurityConfig;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.services.EmailSenderService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ua.foxminded.university.controller")
@Import(SecurityConfig.class)
public class ControllersTestConfig {
    @MockBean
    public PasswordGenerator passwordGenerator;

    @MockBean
    public GroupService groupService;

    @MockBean
    public SubjectService subjectService;

    @MockBean
    public LessonService lessonService;

    @MockBean
    public EmailSenderService emailSenderService;

    @MockBean
    public PasswordEncoder passwordEncoder;

    @MockBean
    public DataSource dataSource;

}

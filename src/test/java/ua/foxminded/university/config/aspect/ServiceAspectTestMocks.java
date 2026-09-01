package ua.foxminded.university.config.aspect;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.repository.StudentRepository;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.repository.TeacherRepository;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.AttendanceService;
import ua.foxminded.university.services.AuthService;
import ua.foxminded.university.services.GradeService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.TokenService;
import ua.foxminded.university.services.impl.UserServiceImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Clock;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MockitoBean(types = {
        StudentRepository.class,
        LessonRepository.class,
        TeacherRepository.class,
        SubjectRepository.class,
        UserRepository.class,
        GradeRepository.class,
        AttendanceRepository.class,
        Clock.class,
        PasswordEncoder.class,
        PasswordGenerator.class,
        JavaMailSender.class,
        TeacherService.class,
        StudentService.class,
        UserServiceImpl.class,
        GradeService.class,
        AttendanceService.class,
        AuthService.class,
        TokenService.class
})
public @interface ServiceAspectTestMocks {
}
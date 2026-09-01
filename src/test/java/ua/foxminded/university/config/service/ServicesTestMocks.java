package ua.foxminded.university.config.service;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.foxminded.university.generator.PasswordGenerator;
import ua.foxminded.university.repository.AttendanceRepository;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.repository.GroupRepository;
import ua.foxminded.university.repository.SubjectRepository;
import ua.foxminded.university.repository.UserRepository;
import ua.foxminded.university.services.AuthService;
import ua.foxminded.university.services.TokenService;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MockitoBean(types = {
        GroupRepository.class,
        SubjectRepository.class,
        PasswordGenerator.class,
        UserRepository.class,
        GradeRepository.class,
        AttendanceRepository.class,
        AuthService.class,
        TokenService.class
})
public @interface ServicesTestMocks {
}
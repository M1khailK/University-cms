package ua.foxminded.university.roleProvider;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class RoleProvider {

    private static final String USERNAME = "username";

    public static Stream<RequestPostProcessor> provideStudentAndTeacherRoles() {
        return Stream.of(
                user(USERNAME).roles("STUDENT"),
                user(USERNAME).roles("TEACHER"));
    }
    public static Stream<RequestPostProcessor> provideAllRoles() {
        return Stream.of(
                user(USERNAME).roles("STUDENT"),
                user(USERNAME).roles("TEACHER"),
                user(USERNAME).roles("ADMIN"),
                anonymous());
    }

}

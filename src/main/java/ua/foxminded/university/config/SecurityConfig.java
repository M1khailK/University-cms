package ua.foxminded.university.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JdbcUserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select email, password, isEnabled from users where email = ? and isEnabled = true");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select u.email, ur.role from users u join user_role ur on u.user_id = ur.user_id where u.email = ?");
        jdbcUserDetailsManager.setRolePrefix("ROLE_");
        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable().authorizeHttpRequests()
                .requestMatchers("/login").anonymous()
                .requestMatchers("/addGrade", "/deleteGrade").hasRole("TEACHER")
                .requestMatchers("/deactivationPage", "/deactivateUser",
                        "/createTeacher", "/createAdmin", "/createStudent", "/createAccount",
                        "/createUniversitySubject", "/createSubject", "/editSubject", "/createUniversityLesson", "/createLesson", "/editLesson",
                        "/getGroupInfo", "/getGroupInfoPage", "/editGroupInfo").hasRole("ADMIN")
                .requestMatchers("/profile", "/settings", "/updatePassword", "/mySchedule").hasAnyRole("STUDENT", "TEACHER")
                .requestMatchers("/getUserSchedule", "/grades").hasAnyRole("ADMIN", "STUDENT", "TEACHER")
                .requestMatchers("/generalSchedule", "/teacherSchedule", "/studentSchedule", "/").permitAll()
                .requestMatchers("/**").permitAll()
                .and().formLogin().loginPage("/login").usernameParameter("email").defaultSuccessUrl("/")
                .and().logout().logoutSuccessUrl("/").and().build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

}

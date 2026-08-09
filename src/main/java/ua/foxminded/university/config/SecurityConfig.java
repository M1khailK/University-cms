package ua.foxminded.university.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select email, password, isEnabled from users where email = ? and isEnabled = true"
        );

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select u.email, ur.role " +
                        "from users u " +
                        "join user_role ur on u.user_id = ur.user_id " +
                        "where u.email = ?"
        );

        jdbcUserDetailsManager.setRolePrefix("ROLE_");

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/subjects")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/subjects/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/subjects/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/subjects", "/api/v1/subjects/{id}")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/groups/{id}/students")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/groups")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/groups/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/groups/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/groups", "/api/v1/groups/{id}")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/lessons")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/lessons/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lessons/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/lessons", "/api/v1/lessons/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/groups",
                                "/api/v1/groups/{id}"
                        )
                        .permitAll()

                        .requestMatchers("/addGrade", "/deleteGrade")
                        .hasRole("TEACHER")

                        .requestMatchers(
                                "/deactivationPage",
                                "/deactivateUser",
                                "/createTeacher",
                                "/createAdmin",
                                "/createStudent",
                                "/createAccount"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/profile",
                                "/settings",
                                "/updatePassword",
                                "/mySchedule"
                        )
                        .hasAnyRole("STUDENT", "TEACHER")

                        .requestMatchers("/getUserSchedule", "/grades")
                        .hasAnyRole("ADMIN", "STUDENT", "TEACHER")

                        .requestMatchers(
                                "/generalSchedule",
                                "/teacherSchedule",
                                "/studentSchedule",
                                "/"
                        )
                        .permitAll()

                        .requestMatchers("/**").permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                )

                .build();
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
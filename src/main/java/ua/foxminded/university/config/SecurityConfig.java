package ua.foxminded.university.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            JdbcUserDetailsManager users,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(users);

        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

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
    public SecurityProblemDetailHandler securityProblemDetailHandler(
            ObjectMapper objectMapper) {

        return new SecurityProblemDetailHandler(objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            SecurityProblemDetailHandler securityProblemDetailHandler) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.GET,
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        )

                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login")
                        .permitAll()

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

                        .requestMatchers(HttpMethod.GET, "/api/v1/schedules/me")
                        .hasAnyRole("STUDENT", "TEACHER")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/profile/password")
                        .hasAnyRole("STUDENT", "TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/profile")
                        .hasAnyRole("STUDENT", "TEACHER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/teachers")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/teachers/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/teachers/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/teachers", "/api/v1/teachers/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/students")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/students/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/students/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/v1/students", "/api/v1/students/{id}")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/admins")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/schedules/options",
                                "/api/v1/schedules/groups/{groupId}",
                                "/api/v1/schedules/teachers/{teacherId}"
                        )
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/grades")
                        .hasRole("TEACHER")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/grades/{id}")
                        .hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/grades/me")
                        .hasRole("STUDENT")

                        .requestMatchers(HttpMethod.GET, "/api/v1/grades")
                        .hasAnyRole("ADMIN", "TEACHER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/attendance-records")
                        .hasRole("TEACHER")

                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(securityProblemDetailHandler)
                        .accessDeniedHandler(securityProblemDetailHandler)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
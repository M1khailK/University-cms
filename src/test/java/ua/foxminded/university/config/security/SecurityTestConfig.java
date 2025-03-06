package ua.foxminded.university.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
public class SecurityTestConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder passwordEncoder = passwordEncoder();

        UserDetails admin = User.withUsername("admin")
                .password("{bcrypt}" + passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails student = User.withUsername("student")
                .password("{bcrypt}" + passwordEncoder.encode("student123"))
                .roles("STUDENT")
                .build();

        UserDetails teacher = User.withUsername("teacher")
                .password("{bcrypt}" + passwordEncoder.encode("teacher123"))
                .roles("TEACHER")
                .build();

        return new InMemoryUserDetailsManager(admin, student, teacher);
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests()
                .requestMatchers("/login").anonymous()
                .requestMatchers("/profile","/settings","/updatePassword","/mySchedule").hasAnyRole("STUDENT", "TEACHER")
                .requestMatchers("/getUserSchedule").hasAnyRole("ADMIN", "STUDENT", "TEACHER")
                .requestMatchers("/generalSchedule","/groupSchedule", "/teacherSchedule", "/studentSchedule", "/").permitAll()
                .requestMatchers("/**").hasRole("ADMIN")
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
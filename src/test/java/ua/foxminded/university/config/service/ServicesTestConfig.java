package ua.foxminded.university.config.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
@ComponentScan({"ua.foxminded.university.services", "ua.foxminded.university.manager"})
public class ServicesTestConfig {

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        return new JavaMailSenderImpl();
    }

}
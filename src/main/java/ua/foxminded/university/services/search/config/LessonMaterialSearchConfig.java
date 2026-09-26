package ua.foxminded.university.services.search.config;

import org.springframework.boot.context.properties
        .EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        LessonMaterialSearchProperties.class
)
public class LessonMaterialSearchConfig {
}
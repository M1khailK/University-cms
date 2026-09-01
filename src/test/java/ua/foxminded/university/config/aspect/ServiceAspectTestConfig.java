package ua.foxminded.university.config.aspect;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@TestConfiguration
@ComponentScan({
        "ua.foxminded.university.aop.service",
        "ua.foxminded.university.services"
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ServiceAspectTestConfig {
}
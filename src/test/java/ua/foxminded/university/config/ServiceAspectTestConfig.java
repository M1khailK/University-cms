package ua.foxminded.university.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootConfiguration()
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"ua.foxminded.university.services","ua.foxminded.university.config","ua.foxminded.university.aop.service"})
public class ServiceAspectTestConfig {
}

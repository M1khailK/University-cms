package ua.foxminded.university.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

@Component
public class ServiceManagerRunnerImpl implements ApplicationRunner {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    @Override
    public void run(ApplicationArguments args) {
        serviceManager.register(studentService.getRole(), studentService);
        serviceManager.register(teacherService.getRole(), teacherService);

    }
}

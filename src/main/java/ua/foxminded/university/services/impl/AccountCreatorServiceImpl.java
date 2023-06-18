package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.AccountCreatorService;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.PasswordService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

@Service
public class AccountCreatorServiceImpl implements AccountCreatorService {

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private PasswordService passwordService;

    public void createTeacherAccount(User user) {
        passwordService.generateAndSendPasswordForUser(user);
        Teacher teacher = new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword());
        teacherService.save(teacher);
    }

    public void createStudentAccount(User user) {
        passwordService.generateAndSendPasswordForUser(user);
        Group group = groupService.getByName(user.getGroupName());
        Student student = new Student(null, user.getFirstName(), user.getLastName(), user.getEmail(), group,
                user.getPassword());
        studentService.save(student);
    }

    public void createAdminAccount(User user) {
        passwordService.generateAndSendPasswordForUser(user);
        Teacher adminTeacher = new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPassword());
        teacherService.saveTeacherAsAdmin(adminTeacher);
    }

}

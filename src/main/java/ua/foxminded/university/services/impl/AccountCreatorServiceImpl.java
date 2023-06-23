package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.DuplicateEmailException;
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

    public void createUserAccount(User user, String role) {
        passwordService.generateAndSendPasswordForUser(user);
        try {
            if (role.equals("STUDENT")) {
                Group group = groupService.getByName(user.getGroupName());
                Student student = new Student(null, user.getFirstName(), user.getLastName(), user.getEmail(), group,
                        user.getPassword(), role);
                studentService.save(student);
            } else {
                Teacher teacher = new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                        user.getPassword(), role);
                teacherService.save(teacher);
            }
        } catch (
                DataIntegrityViolationException exception) {
            throw new DuplicateEmailException("Email already exists. Please choose a different email.");
        }
    }

}

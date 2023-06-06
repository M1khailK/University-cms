package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

@Controller
public class AccountCreatorController {
    private static final String REDIRECT_ACCOUNT_CREATOR_PAGE = "redirect:/accountCreatorPage";
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/accountCreatorPage")
    public String accountCreatorPage() {
        return "accountCreator";
    }

    @PostMapping("/createStudent")
    public String createStudentAccount(@Valid User user) {
        Group group = groupService.getByName(user.getGroupName());
        studentService.save(new Student(null, user.getFirstName(), user.getLastName(), user.getEmail(), group,
                passwordEncoder.encode(user.getPassword())));
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createTeacher")
    public String createTeacherAccount(@Valid User user) {
        teacherService.save(new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                passwordEncoder.encode(user.getPassword())));
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }

    @PostMapping("/createAdmin")
    public String createAdminAccount(@Valid User user) {
        teacherService.saveTeacherAsAdmin(new Teacher(null, user.getFirstName(), user.getLastName(), user.getEmail(),
                passwordEncoder.encode(user.getPassword())));
        return REDIRECT_ACCOUNT_CREATOR_PAGE;
    }
}

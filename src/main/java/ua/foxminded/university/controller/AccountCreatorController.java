package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.foxminded.university.dto.User;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

@Controller
public class AccountCreatorController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    @GetMapping("/accountCreatorPage")
    public String accountCreatorPage() {
        return "accountCreator";
    }

    @PostMapping("/createStudent")
    public String createStudentAccount(@Valid User user) {
        Group group = groupService.getByName(user.getGroupName());
        userService.insertUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        userService.insertStudentById(userService.getUserIdByEmail(user.getEmail()), group.getId());
        return "redirect:/accountCreatorPage";
    }

    @PostMapping("/createTeacher")
    public String createTeacherAccount(@Valid User user) {
        userService.insertUser(user.getFirstName(),user.getLastName(),user.getEmail(),user.getPassword());
        userService.insertTeacherById(userService.getUserIdByEmail(user.getEmail()));
        return "redirect:/accountCreatorPage";
    }
    @PostMapping("/createAdmin")
    public String createAdminAccount(@Valid User user) {
        userService.insertUser(user.getFirstName(),user.getLastName(),user.getEmail(),user.getPassword());
        userService.insertAdmin(userService.getUserIdByEmail(user.getEmail()));
        return "redirect:/accountCreatorPage";
    }
}

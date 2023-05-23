package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String createStudentAccount(@RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("email") String email,
                                       @RequestParam("groupName") String groupName,
                                       @RequestParam("password") String password) {
        Group group = groupService.getByName(groupName);
        userService.insertUser(firstName, lastName, email, password);
        userService.insertStudentById(userService.getUserIdByEmail(email), group.getId());
        return "redirect:/accountCreatorPage";
    }

    @PostMapping("/createTeacher")
    public String createTeacherAccount(@RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password) {
        userService.insertUser(firstName,lastName,email,password);
        userService.insertTeacherById(userService.getUserIdByEmail(email));
        return "redirect:/accountCreatorPage";
    }
    @PostMapping("/createAdmin")
    public String createAdminAccount(@RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password) {
        userService.insertUser(firstName,lastName,email,password);
        userService.insertAdmin(userService.getUserIdByEmail(email));
        return "redirect:/accountCreatorPage";
    }
}

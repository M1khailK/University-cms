package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.foxminded.university.services.PasswordManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

@Controller
public class ProfileController {

    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        passwordManager.register(studentService.getRole(), studentService);
        passwordManager.register(teacherService.getRole(), teacherService);
        UserService userService = passwordManager.getServiceByRole(authentication.getAuthorities().toString()).get();
        userService.getByEmail(authentication.getName());
        model.addAttribute("user", userService.getByEmail(authentication.getName()));
        return "profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {

        return "profileSettings";
    }

}

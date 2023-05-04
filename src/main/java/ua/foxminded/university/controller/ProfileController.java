package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

@Controller
public class ProfileController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String userRole = authentication.getAuthorities().toString();
        if (userRole.equals("[ROLE_TEACHER]")) {
            model.addAttribute("user", teacherService.getByEmail(userEmail).get());
        }
        if (userRole.equals("[ROLE_STUDENT]")) {
            model.addAttribute("user", studentService.getByEmail(userEmail).get());
        }
        return "profile";
    }

}

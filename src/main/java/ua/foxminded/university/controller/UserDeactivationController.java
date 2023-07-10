package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import java.util.List;

@Controller
public class UserDeactivationController {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/deactivationPage")
    public String deactivateUserAccount(Model model) {
        List<Student> students = studentService.getAll();
        List<Teacher> teachers = teacherService.getAll();
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        return "deactivationPage";
    }

    @GetMapping("/deactivateUser")
    public String deactivate(@RequestParam(value = "userId",required = false) Integer userId) {
        userService.disableUserById(userId);
        return "redirect:/deactivationPage";
    }

}

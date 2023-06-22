package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.InvalidUserIdException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserDeactivationController implements CustomExceptionHandler<InvalidUserIdException> {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/deactivationPage")
    public String deactivateUserAccount(Model model) {
        List<Student> students = studentService.getAllEnabledStudents();
        List<Teacher> teachers = teacherService.getAllEnabledTeachers();
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        return "deactivationPage";
    }

    @GetMapping("/deactivateUser")
    public String deactivate(@RequestParam(value = "userId") Integer userId) {
        userService.disableUserById(userId);
        return "redirect:/deactivationPage";
    }

    @Override
    @ExceptionHandler(InvalidUserIdException.class)
    public ModelAndView handleCustomException(InvalidUserIdException ex) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("message", ex.getMessage());

        mav.setViewName("errorPage");

        return mav;
    }
}

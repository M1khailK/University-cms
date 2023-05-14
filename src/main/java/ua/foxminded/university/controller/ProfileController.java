package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.InvalidOldPasswordException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDateTime;

@Controller
public class ProfileController implements CustomExceptionHandler<InvalidOldPasswordException> {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        serviceManager.register(studentService.getRole(), studentService);
        serviceManager.register(teacherService.getRole(), teacherService);
        model.addAttribute("user", serviceManager.getServiceByRole(authentication.getAuthorities().toString()).get().getByEmail(authentication.getName()));
        return "profile";
    }

    @GetMapping("/settings")
    public String settings() {
        return "profileSettings";
    }

    @PostMapping("/updatePassword")
    public String changePassword(@RequestParam("oldPass") String oldPass,
                                 @RequestParam("newPass") String newPass) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        String userRole = authentication.getAuthorities().toString();
        serviceManager.getServiceByRole(userRole).get().changePassword(userEmail, oldPass, newPass);
        return "redirect:/profile";
    }

    @ExceptionHandler(InvalidOldPasswordException.class)
    public ModelAndView handleCustomException(InvalidOldPasswordException ex) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("message", ex.getMessage());

        mav.setViewName("errorPage");

        return mav;
    }

}

package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserManagerService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserScheduleController implements CustomExceptionHandler<InvalidDateRangeException> {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private UserService userService;

    @GetMapping("/mySchedule")
    public String mySchedulePage() {
        return "userSchedule";
    }

    @GetMapping("/getUserSchedule")
    public String showUserSchedule(Model model, @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
                                   @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        if (dateFrom == null && dateTo != null) {
            throw new InvalidDateRangeException("From date cannot be null when To date is provided.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id = userService.getUserIdByEmail(authentication.getName());
        List<UserManagerService> services = serviceManager.getUserManagerServices();
        List<Lesson> userLessons = services.get(0).getLessonsByUserIdAndDateBetween(id, dateFrom, dateTo);
        model.addAttribute("userLessons", userLessons);
        return "userSchedule";
    }

    @Override
    @ExceptionHandler(InvalidDateRangeException.class)
    public ModelAndView handleCustomException(InvalidDateRangeException exception) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("message", exception.getMessage());

        mav.setViewName("errorPage");

        return mav;
    }
}

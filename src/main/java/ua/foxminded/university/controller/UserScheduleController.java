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
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserScheduleController implements CustomExceptionHandler<InvalidDateRangeException> {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private UserService userService;

    @GetMapping("/mySchedule")
    public String mySchedulePage() {
        return "userSchedule";
    }

    @GetMapping("/getUserSchedule")
    public String showUserSchedule(Model model, @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
                                   @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        List<Lesson> userLessons = userService.getUserLessons(dateFrom, dateTo);
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

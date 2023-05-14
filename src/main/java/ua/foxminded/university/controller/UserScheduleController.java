package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.manager.ServiceManager;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class UserScheduleController {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonService lessonService;

    @GetMapping("/mySchedule")
    public String mySchedulePage(Model model) {

        return "userSchedule";
    }

    @GetMapping("/getUserSchedule")
    public String showUserSchedule(Model model, @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
                                   @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().toString();
        serviceManager.register(studentService.getRole(), studentService);
        serviceManager.register(teacherService.getRole(), teacherService);
        Integer id = serviceManager.getServiceByRole(authentication.getAuthorities().toString()).get().getUserIdByEmail(authentication.getName());
        List<Lesson> userLessons = serviceManager.getServiceByRole(authentication.getAuthorities().toString()).get().getLessonsByUserIdAndDateBetween(id, dateFrom, dateTo);
        model.addAttribute("userLessons", userLessons);
        return "userSchedule";
    }
}

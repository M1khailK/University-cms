package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.foxminded.university.customexceptions.InvalidDateRangeException;
import ua.foxminded.university.customexceptions.handler.CustomExceptionHandler;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ScheduleController implements CustomExceptionHandler<InvalidDateRangeException> {

    private static final String GROUPS_ATTRIBUTE = "groups";
    private static final String TEACHERS_ATTRIBUTE = "teachers";
    private static final String GENERAL_SCHEDULE = "generalSchedule";
    private static final String REDIRECT_GENERAL_SCHEDULE = "redirect:/generalSchedule";
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private GroupService groupService;

    @GetMapping("/generalSchedule")
    public String generalSchedule(Model model) {
        List<Group> groups = groupService.getAll();
        List<Teacher> teachers = teacherService.getAll();

        model.addAttribute(GROUPS_ATTRIBUTE, groups);
        model.addAttribute(TEACHERS_ATTRIBUTE, teachers);

        return GENERAL_SCHEDULE;
    }

    @GetMapping("/teacherSchedule")
    public String showTeacherSchedule(Model model, @RequestParam(value = "teacherId", required = false) Integer teacherId,
                                      @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
                                      @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        if (teacherId == null) {
            return REDIRECT_GENERAL_SCHEDULE;
        } else if (dateFrom == null && dateTo != null) {
            throw new InvalidDateRangeException("From date cannot be null when To date is provided.");
        }
        Teacher teacher = teacherService.getById(teacherId);

        List<Teacher> teachers = teacherService.getAll();
        List<Group> groups = groupService.getAll();
        List<Lesson> teacherLessons = lessonService.getAllByTeacherAndDateBetween(teacher, dateFrom, dateTo);

        model.addAttribute(TEACHERS_ATTRIBUTE, teachers);
        model.addAttribute(GROUPS_ATTRIBUTE, groups);
        model.addAttribute("teacherLessons", teacherLessons);

        return GENERAL_SCHEDULE;
    }

    @GetMapping("/groupSchedule")
    public String showGroupSchedule(Model model, @RequestParam(value = "groupId", required = false) Integer groupId,
                                    @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
                                    @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        if (groupId == null) {
            return REDIRECT_GENERAL_SCHEDULE;
        } else if (dateFrom == null && dateTo != null) {
            throw new InvalidDateRangeException("From date cannot be null when To date is provided.");
        }

        Group group = groupService.getById(groupId);

        List<Group> groups = groupService.getAll();
        List<Teacher> teachers = teacherService.getAll();
        List<Lesson> groupLessons = lessonService.getAllByGroupAndDateBetween(group, dateFrom, dateTo);

        model.addAttribute(GROUPS_ATTRIBUTE, groups);
        model.addAttribute(TEACHERS_ATTRIBUTE, teachers);
        model.addAttribute("groupLessons", groupLessons);

        return GENERAL_SCHEDULE;
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

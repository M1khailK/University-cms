package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ScheduleController {

    private static final String GROUPS = "groups";
    private static final String TEACHERS = "teachers";
    private static final String GENERAL_SCHEDULE = "generalSchedule";
    private static final String DEFAULT_ID_VALUE = "-1";
    private static final Integer USER_DID_NOT_SELECT_ENTITY = -1;
    private static final String REDIRECT_GENERAL_SCHEDULE = "redirect:/generalSchedule";
    private static final int NUMBER_TO_DISPLAY_TOMORROW_SCHEDULE = 18;
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
        model.addAttribute(GROUPS, groups);
        model.addAttribute(TEACHERS, teachers);
        return GENERAL_SCHEDULE;
    }

    @GetMapping("/teacherSchedule")
    public String showTeacherSchedule(Model model, @RequestParam(value = "teacherId", defaultValue = DEFAULT_ID_VALUE) Integer teacherId, @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom, @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        if (teacherId.equals(USER_DID_NOT_SELECT_ENTITY)) {
            return REDIRECT_GENERAL_SCHEDULE;
        }
        Teacher teacher = teacherService.getById(teacherId).get();
        List<Teacher> teachers = teacherService.getAll();
        List<Group> groups = groupService.getAll();
        List<Lesson> teacherLessons;
        if (dateFrom == null && dateTo == null) {
            LocalDate today = LocalDate.now();
            if (LocalTime.now().isAfter(LocalTime.of(NUMBER_TO_DISPLAY_TOMORROW_SCHEDULE, 0))) {
                dateFrom = today.plusDays(1);
                dateTo = today.plusDays(1);
            } else {
                dateFrom = today;
                dateTo = today;
            }
        }
        teacherLessons = lessonService.getAllByTeacherAndDateBetween(teacher, dateFrom, dateTo);
        model.addAttribute(TEACHERS, teachers);
        model.addAttribute(GROUPS, groups);
        model.addAttribute("teacherLessons", teacherLessons);
        return GENERAL_SCHEDULE;
    }

    @GetMapping("/groupSchedule")
    public String showGroupSchedule(Model model, @RequestParam(value = "groupId", defaultValue = DEFAULT_ID_VALUE) Integer groupId, @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom, @RequestParam(value = "dateTo", required = false) LocalDate dateTo) {
        if (groupId.equals(USER_DID_NOT_SELECT_ENTITY)) {
            return REDIRECT_GENERAL_SCHEDULE;
        }
        Group group = groupService.getById(groupId).get();
        List<Group> groups = groupService.getAll();
        List<Teacher> teachers = teacherService.getAll();
        List<Lesson> groupLessons;
        if (dateFrom == null && dateTo == null) {
            LocalDate today = LocalDate.now();
            if (LocalTime.now().isAfter(LocalTime.of(NUMBER_TO_DISPLAY_TOMORROW_SCHEDULE, 0))) {
                dateFrom = today.plusDays(1);
                dateTo = today.plusDays(1);
            } else {
                dateFrom = today;
                dateTo = today;
            }
        }
        groupLessons = lessonService.getAllByGroupAndDateBetween(group, dateFrom, dateTo);
        model.addAttribute(GROUPS, groups);
        model.addAttribute(TEACHERS, teachers);

        model.addAttribute("groupLessons", groupLessons);
        return GENERAL_SCHEDULE;
    }
}

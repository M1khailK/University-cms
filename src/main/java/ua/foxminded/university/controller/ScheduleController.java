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
import java.util.List;

@Controller
public class ScheduleController {

    private static final String GROUPS = "groups";
    private static final String TEACHERS = "teachers";
    private static final String GENERAL_SCHEDULE = "generalSchedule";
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
    public String showTeacherSchedule(Model model, @RequestParam("teacherId") Integer teacherId, @RequestParam("dateFrom") LocalDate dateFrom, @RequestParam("dateTo") LocalDate dateTo) {
        Teacher teacher = teacherService.getById(teacherId).get();
        List<Teacher> teachers = teacherService.getAll();
        List<Group> groups = groupService.getAll();
        List<Lesson> teacherLessons = lessonService.getAllByTeacherAndDateBetween(teacher, dateFrom, dateTo);
        model.addAttribute(TEACHERS, teachers);
        model.addAttribute(GROUPS, groups);
        model.addAttribute("teacherLessons", teacherLessons);
        return GENERAL_SCHEDULE;
    }

    @GetMapping("/groupSchedule")
    public String showGroupSchedule(Model model, @RequestParam("groupId") Integer groupId, @RequestParam("dateFrom") LocalDate dateFrom, @RequestParam("dateTo") LocalDate dateTo) {
        Group group = groupService.getById(groupId).get();
        List<Group> groups = groupService.getAll();
        List<Teacher> teachers = teacherService.getAll();

        List<Lesson> groupLessons = lessonService.getAllByGroupAndDateBetween(group, dateFrom, dateTo);
        model.addAttribute(GROUPS, groups);
        model.addAttribute(TEACHERS, teachers);

        model.addAttribute("groupLessons", groupLessons);
        return GENERAL_SCHEDULE;
    }
}

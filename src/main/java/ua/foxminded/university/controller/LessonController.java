package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class LessonController {

    private static final String REDIRECT_CREATE_LESSON = "redirect:/createUniversityLesson";

    @Autowired
    private LessonService lessonService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/createUniversityLesson")
    public String createLessonPage(Model model) {
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("groups", groupService.getAll());
        model.addAttribute("subjects", subjectService.getAll());
        return "lessonCreator";
    }

    @PostMapping("/createLesson")
    public String createLesson(@RequestParam String name, @RequestParam(value = "date", required = false) LocalDate date,
                               @RequestParam(value = "startTime", required = false) LocalTime startTime,
                               @RequestParam(value = "endTime", required = false) LocalTime endTime,
                               @RequestParam(value = "subjectId", required = false) Integer subjectId,
                               @RequestParam(value = "groupId", required = false) Integer groupId,
                               @RequestParam(value = "teacherId", required = false) Integer teacherId) {
        Lesson lesson = new Lesson(null, name, date, startTime, endTime, subjectService.getById(subjectId),
                groupService.getById(groupId), teacherService.getById(teacherId));
        lessonService.save(lesson);
        return REDIRECT_CREATE_LESSON;
    }
}

package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.dto.LessonDTO;
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
    public String createLesson(@Valid LessonDTO lessonDTO) {
        Lesson lesson = new Lesson(null, lessonDTO.getName(), lessonDTO.getDate(), lessonDTO.getStartTime(), lessonDTO.getEndTime(), lessonDTO.getSubject(), lessonDTO.getGroup(), lessonDTO.getTeacher());
        lessonService.save(lesson);
        lessonService.getAll().forEach(lesson1 -> System.out.println(lesson1));
        return REDIRECT_CREATE_LESSON;
    }
}

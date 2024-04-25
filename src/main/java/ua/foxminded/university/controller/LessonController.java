package ua.foxminded.university.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.mapper.LessonMapper;
import ua.foxminded.university.services.GroupService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.SubjectService;
import ua.foxminded.university.services.TeacherService;

@Controller
public class LessonController {

    private static final String REDIRECT_CREATE_LESSON = "redirect:/createUniversityLesson";
    private static final String LESSONS_ATTRIBUTE = "lessons";
    private static final String SUBJECTS_ATTRIBUTE = "subjects";
    private static final String GROUPS_ATTRIBUTE = "groups";
    private static final String TEACHERS_ATTRIBUTE = "teachers";

    @Autowired
    private LessonService lessonService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonMapper lessonMapper;

    @GetMapping("/createUniversityLesson")
    public String createLessonPage(Model model) {
        model.addAttribute(TEACHERS_ATTRIBUTE, teacherService.getAll());
        model.addAttribute(GROUPS_ATTRIBUTE, groupService.getAll());
        model.addAttribute(SUBJECTS_ATTRIBUTE, subjectService.getAll());
        model.addAttribute(LESSONS_ATTRIBUTE, lessonService.getAll());
        return "lessonCreator";
    }

    @PostMapping("/createLesson")
    public String createLesson(@Valid LessonDTO lessonDTO) {
        Lesson lesson = lessonMapper
                .toLesson(lessonDTO);
        lessonService
                .save(lesson);
        return REDIRECT_CREATE_LESSON;
    }

    @PostMapping("/editLesson")
    public String editLesson(@Valid LessonDTO lessonDTO) {
        Lesson lesson = lessonMapper.toLesson(lessonDTO);
        lessonService.save(lesson);
        return REDIRECT_CREATE_LESSON;
    }
}

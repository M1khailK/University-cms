package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;
import ua.foxminded.university.services.impl.GradeServiceImpl;

import java.util.List;

@Controller
public class GradeController {
    @Autowired
    private GradeServiceImpl gradeService;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonService lessonService;
    @GetMapping("/grades")
    public String showGrades(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if ("[ROLE_TEACHER]".equals(auth.getAuthorities().toString())) {
            List<Grade> allGrades = gradeService.getAllGrades();
            model.addAttribute("grades", allGrades);
        } else if ("[ROLE_STUDENT]".equals(auth.getAuthorities().toString())) {
            List<Grade> studentGrades = gradeService.getGradesByEmail(auth.getName());
            model.addAttribute("grades", studentGrades);
        }
        return "gradesView";
    }
    @PostMapping("/addGrade")
    public String addGrade(@RequestParam Integer studentId, @RequestParam Integer lessonId, @RequestParam Integer gradeValue) {
        Grade grade = new Grade(null, studentService.getById(studentId), lessonService.getById(lessonId), gradeValue);
        gradeService.addGrade(grade);
        return "redirect:/grades";
    }

    @GetMapping("/deleteGrade")
    public String deleteGrade(@RequestParam Integer gradeId) {
        gradeService.deleteGrade(gradeId);
        return "redirect:/grades";
    }
}

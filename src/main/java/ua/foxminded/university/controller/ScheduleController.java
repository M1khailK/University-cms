package ua.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ScheduleController {

    private static final String STUDENTS = "students";
    private static final String TEACHERS = "teachers";
    private static final String GENERAL_SCHEDULE = "generalSchedule";
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/generalSchedule")
    public String generalSchedule(Model model) {
        List<Student> students = studentService.getAll();
        List<Teacher> teachers = teacherService.getAll();
        model.addAttribute(STUDENTS, students);
        model.addAttribute(TEACHERS, teachers);
        return GENERAL_SCHEDULE;
    }

    @GetMapping("/teacherSchedule")
    public String showTeacherSchedule(Model model, @RequestParam("teacherId") Integer teacherId, @RequestParam("dateFrom") LocalDate dateFrom, @RequestParam("dateTo") LocalDate dateTo) {
        Teacher teacher = teacherService.getById(teacherId).get();
        List<Teacher> teachers = teacherService.getAll();
        List<Student> students = studentService.getAll();
        List<Lesson> teacherLessons = lessonService.getAllByTeacherAndDateBetween(teacher, dateFrom, dateTo);
        model.addAttribute(TEACHERS, teachers);
        model.addAttribute(STUDENTS, students);
        model.addAttribute("teacherLessons", teacherLessons);
        return GENERAL_SCHEDULE;
    }

    @GetMapping("/studentSchedule")
    public String showStudentSchedule(Model model, @RequestParam("studentId") Integer studentId, @RequestParam("dateFrom") LocalDate dateFrom, @RequestParam("dateTo") LocalDate dateTo) {
        Student student = studentService.getById(studentId).get();
        List<Student> students = studentService.getAll();
        List<Teacher> teachers = teacherService.getAll();

        List<Lesson> studentLessons = lessonService.getAllByStudentAndDateBetween(student, dateFrom, dateTo);
        model.addAttribute(STUDENTS, students);
        model.addAttribute(TEACHERS, teachers);

        model.addAttribute("studentLessons", studentLessons);
        return GENERAL_SCHEDULE;
    }
}

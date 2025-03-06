package ua.foxminded.university.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.services.GradeService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.TeacherService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GradeController {
    @Autowired
    private GradeService gradeService;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ObjectMapper objectMapper;

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

    @GetMapping("/grades/analytics")
    public String showAnalytics(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Lesson> lessons = lessonService.getAll();

        // Группируем уроки по названию
        Map<String, List<Lesson>> lessonsByName = lessons.stream()
                .collect(Collectors.groupingBy(Lesson::getName));

        List<String> lessonNames = new ArrayList<>();
        List<Double> averageGrades = new ArrayList<>();
        List<Long> gradeCounts = new ArrayList<>();

        for (String name : lessonsByName.keySet()) {
            List<Lesson> groupedLessons = lessonsByName.get(name);

            // Получаем все оценки для сгруппированных уроков
            List<Grade> gradesForGroupedLessons = groupedLessons.stream()
                    .flatMap(lesson -> gradeService.getGradesByLessonId(lesson.getId()).stream())
                    .collect(Collectors.toList());

            // Вычисляем среднюю оценку и количество оценок
            double avgGrade = gradesForGroupedLessons.stream()
                    .collect(Collectors.averagingDouble(Grade::getValue));
            long count = gradesForGroupedLessons.size();

            lessonNames.add(name);
            averageGrades.add(avgGrade);
            gradeCounts.add(count);
        }

        List<Student> students = studentService.getAll();
        List<String> studentNames = new ArrayList<>();
        List<Double> averageGradesByStudent = new ArrayList<>();

        for (Student student : students) {
            String name = student.getFirstName() + " " + student.getLastName();
            Double avgGrade = gradeService.getAverageGradeByStudent(student.getId());

            if (avgGrade != null) {
                studentNames.add(name);
                averageGradesByStudent.add(avgGrade);
            }
        }

        List<Long> gradeDistribution = gradeService.getGradeDistribution();

        model.addAttribute("lessonNames", objectMapper.writeValueAsString(lessonNames));
        model.addAttribute("averageGrades", objectMapper.writeValueAsString(averageGrades));
        model.addAttribute("gradeCounts", objectMapper.writeValueAsString(gradeCounts));
        model.addAttribute("studentNames", objectMapper.writeValueAsString(studentNames));
        model.addAttribute("averageGradesByStudent", objectMapper.writeValueAsString(averageGradesByStudent));
        model.addAttribute("gradeDistribution", objectMapper.writeValueAsString(gradeDistribution));

        return "gradesAnalyticsView";
    }

    @GetMapping("/attendance/analytics")
    public String showAttendanceAnalytics(Model model) {
        List<Lesson> lessons = lessonService.getAll();
        List<Student> students = studentService.getAll();

        List<String> attendanceData = lessons.stream()
                .flatMap(lesson -> students.stream()
                        .filter(student -> !gradeService.getGradesByStudentAndLesson(student.getId(), lesson.getId()).isEmpty())
                        .map(student -> "Lesson: " + lesson.getName() + ", Student: " + student.getFirstName() + " " + student.getLastName()))
                .collect(Collectors.toList());

        List<String> absenceData = lessons.stream()
                .flatMap(lesson -> students.stream()
                        .filter(student -> gradeService.getGradesByStudentAndLesson(student.getId(), lesson.getId()).isEmpty())
                        .map(student -> "Lesson: " + lesson.getName() + ", Student: " + student.getFirstName() + " " + student.getLastName()))
                .collect(Collectors.toList());

        model.addAttribute("attendanceData", attendanceData);
        model.addAttribute("absenceData", absenceData);

        return "attendanceAnalyticsView";
    }


    @GetMapping("/grades/filterByLesson")
    public String filterByLesson(@RequestParam Integer lessonId, Model model) {
        List<Grade> filteredGrades = gradeService.getGradesByLessonId(lessonId);
        model.addAttribute("grades", filteredGrades);
        return "gradesView";
    }

    @GetMapping("/grades/sortByGrade")
    public String sortByGrade(@RequestParam String order, Model model) {
        List<Grade> sortedGrades = gradeService.getGradesSortedByValue(order);
        model.addAttribute("grades", sortedGrades);
        return "gradesView";
    }

    @GetMapping("/grades/filterByDate")
    public String filterByDate(@RequestParam String startDate, @RequestParam String endDate, Model model) {
        List<Grade> filteredGrades = gradeService.getGradesByDateRange(LocalDate.parse(startDate), LocalDate.parse(endDate));
        model.addAttribute("grades", filteredGrades);
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

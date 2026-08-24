package ua.foxminded.university.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.customexceptions.GradeAccessDeniedException;
import ua.foxminded.university.customexceptions.GradeNotFoundException;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.services.GradeService;
import ua.foxminded.university.services.LessonService;
import ua.foxminded.university.services.StudentService;
import ua.foxminded.university.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private StudentService studentService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private UserService userService;
    @Autowired
    private GradeRepository gradeRepository;

    public List<Grade> getGradesByEmail(String studentEmail) {
        return gradeRepository.findByStudentEmail(studentEmail);
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public void addGrade(Grade grade) {
        gradeRepository.save(grade);
    }

    public void deleteGrade(Integer gradeId) {
        gradeRepository.deleteById(gradeId);
    }

    public List<Grade> getGradesByLessonId(Integer lessonId) {
        return gradeRepository.findByLessonId(lessonId);
    }

    public List<Grade> getGradesSortedByValue(String order) {
        return order.equals("asc") ? gradeRepository.findAllByOrderByValueAsc() : gradeRepository.findAllByOrderByValueDesc();
    }

    public List<Grade> getGradesByDateRange(LocalDate startDate, LocalDate endDate) {
        return gradeRepository.findByLessonDateBetween(startDate, endDate);
    }

    public Double getAverageGradeByStudent(Integer studentId) {
        return gradeRepository.findAverageGradeByStudentId(studentId);
    }

    public List<Long> getGradeDistribution() {
        return gradeRepository.findGradeDistribution();
    }

    public Double getAverageGradeByLesson(Integer lessonId) {
        return gradeRepository.findAverageGradeByLessonId(lessonId);
    }

    public Long getGradeCountByLesson(Integer lessonId) {
        return gradeRepository.findGradeCountByLessonId(lessonId);
    }

    public List<Grade> getGradesByStudentAndLesson(Integer studentId, Integer lessonId) {
        return gradeRepository.findByStudentIdAndLessonId(studentId, lessonId);
    }

    @Override
    @Transactional
    public Grade createGrade(Integer studentId, Integer lessonId, Integer value, String teacherEmail) {
        Lesson lesson = lessonService.getById(lessonId);
        assertTeacherOwnsLesson(lesson, teacherEmail);

        Grade grade = new Grade(
                null,
                studentService.getById(studentId),
                lesson,
                value
        );

        return gradeRepository.save(grade);
    }

    @Override
    @Transactional
    public void deleteGrade(Integer gradeId, String teacherEmail) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new GradeNotFoundException(gradeId));

        assertTeacherOwnsLesson(grade.getLesson(), teacherEmail);

        gradeRepository.delete(grade);
    }

    private void assertTeacherOwnsLesson(Lesson lesson, String teacherEmail) {
        int teacherId = userService.getUserIdByEmail(teacherEmail);

        if (lesson.getTeacher() == null || !Objects.equals(lesson.getTeacher().getId(), teacherId)) {
            throw new GradeAccessDeniedException(lesson.getId());
        }
    }

}

package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.repository.GradeRepository;
import ua.foxminded.university.services.GradeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {
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

}

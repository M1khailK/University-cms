package ua.foxminded.university.services;

import ua.foxminded.university.info.Grade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GradeService {
    List<Grade> getGradesByEmail(String studentEmail);

    List<Grade> getAllGrades();

    void addGrade(Grade grade);

    void deleteGrade(Integer gradeId);

    List<Grade> getGradesByLessonId(Integer lessonId);

    List<Grade> getGradesSortedByValue(String order);

    List<Grade> getGradesByDateRange(LocalDate parse, LocalDate parse1);

    Long getGradeCountByLesson(Integer id);

    Double getAverageGradeByLesson(Integer id);

    Double getAverageGradeByStudent(Integer id);

    List<Long> getGradeDistribution();
    List<Grade> getGradesByStudentAndLesson(Integer studentId, Integer lessonId);

}

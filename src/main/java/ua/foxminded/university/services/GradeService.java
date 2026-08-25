package ua.foxminded.university.services;

import ua.foxminded.university.info.Grade;

import java.util.List;

public interface GradeService {

    Grade createGrade(Integer studentId, Integer lessonId, Integer value, String teacherEmail);

    void deleteGrade(Integer gradeId, String teacherEmail);

    List<Grade> getGradesByEmail(String studentEmail);

    List<Grade> getAllGrades();
}

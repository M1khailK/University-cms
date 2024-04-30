package ua.foxminded.university.services;

import ua.foxminded.university.info.Grade;

import java.util.List;

public interface GradeService {
    List<Grade> getGradesByEmail(String studentEmail);

    List<Grade> getAllGrades();

    void addGrade(Grade grade);

    void deleteGrade(Integer gradeId);
}

package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Grade;
import ua.foxminded.university.repository.GradeRepository;

import java.util.List;

@Service
public class GradeService {
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


}

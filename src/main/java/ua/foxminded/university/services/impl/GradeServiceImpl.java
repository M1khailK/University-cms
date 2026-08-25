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

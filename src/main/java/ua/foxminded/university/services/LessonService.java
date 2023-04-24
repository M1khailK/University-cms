package ua.foxminded.university.services;

import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface LessonService extends EntityService<Lesson> {

    List<Lesson> getAllByStudentAndDateBetween(Student student, LocalDate from, LocalDate to);

    List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to);

    List<Lesson> getAllBySubjectAndDateBetween(Subject subject, LocalDate from, LocalDate to);

}

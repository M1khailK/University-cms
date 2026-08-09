package ua.foxminded.university.services;

import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface LessonService extends EntityService<Lesson> {

    List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to);

    List<Lesson> getAllByGroupAndDateBetween(Group group, LocalDate from, LocalDate to);

    Lesson create(Lesson lesson);

    Lesson update(int lessonId, Lesson lesson);

    void deleteById(int lessonId);
}

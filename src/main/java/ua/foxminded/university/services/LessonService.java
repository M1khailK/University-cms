package ua.foxminded.university.services;

import ua.foxminded.university.dto.LessonDTO;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LessonService extends EntityService<Lesson> {

    List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to);

    List<Lesson> getAllByGroupAndDateBetween(Group group, LocalDate from, LocalDate to);

}

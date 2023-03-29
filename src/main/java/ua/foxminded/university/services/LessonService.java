package ua.foxminded.university.services;

import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonService extends EntityService<Lesson> {

    List<Lesson> getAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    List<Lesson> getAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);

    List<Lesson> getLessonsBySubjectId(Integer subjectId);

}

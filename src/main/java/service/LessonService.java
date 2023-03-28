package service;

import info.Lesson;
import info.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonService {

    void save(Lesson lesson);

    Optional<Subject> getById(Integer lessonId);

    List<Lesson> getAll();

    List<Lesson> getAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    List<Lesson> getAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);

    List<Lesson> getLessonsBySubjectId(Integer subjectId);

    void deleteById(Integer lessonId);

}

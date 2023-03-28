package service;

import info.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonService extends Service<Lesson> {

    List<Lesson> getAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    List<Lesson> getAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);

    List<Lesson> getLessonsBySubjectId(Integer subjectId);

}

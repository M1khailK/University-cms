package service.impl;

import info.Lesson;
import repository.LessonRepository;
import service.LessonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LessonServiceImpl implements LessonService {

    private LessonRepository lessonRepository;

    @Override
    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    @Override
    public Optional<Lesson> getById(Integer lessonId) {
        return lessonRepository.findById(lessonId);
    }

    @Override
    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to) {
        return lessonRepository.findAllByGroupIdAndDateBetween(groupId, from, to);
    }

    @Override
    public List<Lesson> getAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to) {
        return lessonRepository.findAllByTeacherIdAndDateBetween(teacherId, from, to);
    }

    @Override
    public List<Lesson> getLessonsBySubjectId(Integer subjectId) {
        return lessonRepository.findAllBySubjectId(subjectId);
    }

    @Override
    public void deleteById(Integer lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}

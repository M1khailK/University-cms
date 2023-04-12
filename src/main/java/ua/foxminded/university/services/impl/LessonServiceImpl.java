package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.services.LessonService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
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
    public List<Lesson> getLessonsBySubjectIdAndDateBetween(Integer subjectId, LocalDate from, LocalDate to) {
        return lessonRepository.findAllBySubjectIdAndDateBetween(subjectId, from, to);
    }

    @Override
    public void deleteById(Integer lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}

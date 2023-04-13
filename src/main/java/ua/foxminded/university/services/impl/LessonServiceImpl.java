package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
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
    public List<Lesson> getAllByGroupAndDateBetween(Group group, LocalDate from, LocalDate to) {
        return lessonRepository.findAllByGroupIdAndDateBetween(group.getId(), from, to);
    }

    @Override
    public List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to) {
        return lessonRepository.findAllByTeacherIdAndDateBetween(teacher.getId(), from, to);
    }

    @Override
    public List<Lesson> getLessonsBySubjectAndDateBetween(Subject subject, LocalDate from, LocalDate to) {
        return lessonRepository.findAllBySubjectIdAndDateBetween(subject.getId(), from, to);
    }

    @Override
    public void deleteById(Integer lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}

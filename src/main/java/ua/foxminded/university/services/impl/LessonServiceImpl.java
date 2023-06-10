package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.services.LessonService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private static final int HOUR_TO_DISPLAY_TOMORROW_SCHEDULE = 18;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private Clock clock;

    @Override
    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    @Override
    public Lesson getById(Integer lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("Lesson was not found by id"));
    }

    @Override
    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to) {
        if (from == null) {
            from = getDefaultDate();
        }
        if (to == null) {
            to = from.plusDays(1);
        }
        return lessonRepository.findAllByTeacherIdAndDateBetween(teacher.getId(), from, to).orElseThrow(() -> new IllegalArgumentException("Lessons were not found by teacher's id and date between"));
    }

    @Override
    public List<Lesson> getAllByGroupAndDateBetween(Group group, LocalDate from, LocalDate to) {
        if (from == null) {
            from = getDefaultDate();
        }
        if (to == null) {
            to = from;
        }
        return lessonRepository.findAllByGroupIdAndDateBetween(group.getId(), from, to).orElseThrow(() -> new IllegalArgumentException("Lessons were not found by group's id and date between"));
    }

    private LocalDate getDefaultDate() {
        LocalDate today = LocalDate.now(clock);
        if (LocalTime.now(clock).isAfter(LocalTime.of(HOUR_TO_DISPLAY_TOMORROW_SCHEDULE, 0, 0, 0))) {
            return today.plusDays(1);
        } else {
            return today;
        }
    }

    @Override
    public void deleteById(Integer lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}

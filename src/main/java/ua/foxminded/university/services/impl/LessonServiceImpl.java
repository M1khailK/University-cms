package ua.foxminded.university.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.foxminded.university.info.Group;
import ua.foxminded.university.info.Lesson;
import ua.foxminded.university.info.Student;
import ua.foxminded.university.info.Subject;
import ua.foxminded.university.info.Teacher;
import ua.foxminded.university.repository.LessonRepository;
import ua.foxminded.university.services.LessonService;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<Lesson> getById(Integer lessonId) {
        return lessonRepository.findById(lessonId);
    }

    @Override
    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getAllByStudentAndDateBetween(Student student, LocalDate from, LocalDate to) {
        if (student.getGroup() == null) {
            throw new IllegalArgumentException("Student group can`t be null.");
        }
        return lessonRepository.findAllByGroupIdAndDateBetween(student.getGroup().getId(), from, to);
    }

    @Override
    public List<Lesson> getAllByTeacherAndDateBetween(Teacher teacher, LocalDate from, LocalDate to) {
        if (to == null && from != null) {
            return lessonRepository.findAllByTeacherIdAndDateBetween(teacher.getId(), from, from.plusDays(1));
        }
        return lessonRepository.findAllByTeacherIdAndDateBetween(teacher.getId(), setTodayOrTomorrowDate(from), setTodayOrTomorrowDate(to));
    }

    @Override
    public List<Lesson> getAllBySubjectAndDateBetween(Subject subject, LocalDate from, LocalDate to) {
        return lessonRepository.findAllBySubjectIdAndDateBetween(subject.getId(), from, to);
    }

    @Override
    public List<Lesson> getAllByGroupAndDateBetween(Group group, LocalDate from, LocalDate to) {
        if (to == null && from != null) {
            return lessonRepository.findAllByGroupIdAndDateBetween(group.getId(), from, from.plusDays(1));
        }
        return lessonRepository.findAllByGroupIdAndDateBetween(group.getId(), setTodayOrTomorrowDate(from), setTodayOrTomorrowDate(to));
    }

    private LocalDate setTodayOrTomorrowDate(LocalDate date) {
        if (date == null) {
            LocalDate today = LocalDate.now(clock);
            if (LocalTime.now(clock).isAfter(LocalTime.of(HOUR_TO_DISPLAY_TOMORROW_SCHEDULE, 0, 0, 0))) {
                date = today.plusDays(1);
            } else {
                date = today;
            }
        }
        return date;
    }

    @Override
    public void deleteById(Integer lessonId) {
        lessonRepository.deleteById(lessonId);
    }
}

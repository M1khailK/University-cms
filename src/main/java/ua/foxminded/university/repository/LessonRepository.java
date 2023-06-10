package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    Optional<List<Lesson>> findAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    Optional<List<Lesson>> findAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);
}

package repository;

import info.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    List<Lesson> findAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);
}

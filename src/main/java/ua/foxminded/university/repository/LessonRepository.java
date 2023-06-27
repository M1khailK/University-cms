package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findAllByGroupIdAndDateBetween(Integer groupId, LocalDate from, LocalDate to);

    List<Lesson> findAllByTeacherIdAndDateBetween(Integer teacherId, LocalDate from, LocalDate to);
}

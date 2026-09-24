package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.university.info.Lesson;

import java.time.LocalDate;
import java.util.List;

public interface LessonRepository
        extends JpaRepository<Lesson, Integer> {

    List<Lesson> findAllByGroupIdAndDateBetween(
            Integer groupId,
            LocalDate from,
            LocalDate to
    );

    List<Lesson> findAllByTeacherIdAndDateBetween(
            Integer teacherId,
            LocalDate from,
            LocalDate to
    );

    @Query("""
            select distinct lesson.id
            from Lesson lesson
            join lesson.group lessonGroup
            join lessonGroup.students student
            where student.email = :email
            order by lesson.id
            """)
    List<Integer> findIdsAccessibleToStudent(
            @Param("email") String email
    );

    @Query("""
            select lesson.id
            from Lesson lesson
            join lesson.teacher teacher
            where teacher.email = :email
            order by lesson.id
            """)
    List<Integer> findIdsAccessibleToTeacher(
            @Param("email") String email
    );

    @Query("""
            select lesson.id
            from Lesson lesson
            order by lesson.id
            """)
    List<Integer> findAllIds();
}
    package ua.foxminded.university.repository;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;
    import ua.foxminded.university.info.Grade;

    import java.time.LocalDate;
    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface GradeRepository extends JpaRepository<Grade, Integer> {
        List<Grade> findByStudentEmail(String studentEmail);
        List<Grade> findByLessonId(Integer lessonId);
        List<Grade> getGradesByLessonId(Integer lessonId);
        List<Grade> findAllByOrderByValueAsc();
        List<Grade> findAllByOrderByValueDesc();
        List<Grade> findByLessonDateBetween(LocalDate startDate, LocalDate endDate);
        @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId")
        Double findAverageGradeByStudentId(@Param("studentId") Integer studentId);

        @Query("SELECT COUNT(g) FROM Grade g GROUP BY g.value ORDER BY g.value")
        List<Long> findGradeDistribution();
        @Query("SELECT AVG(g.value) FROM Grade g WHERE g.lesson.id = :lessonId")
        Double findAverageGradeByLessonId(@Param("lessonId") Integer lessonId);

        @Query("SELECT COUNT(g) FROM Grade g WHERE g.lesson.id = :lessonId")
        Long findGradeCountByLessonId(@Param("lessonId") Integer lessonId);
        List<Grade> findByStudentIdAndLessonId(Integer studentId, Integer lessonId);

    }

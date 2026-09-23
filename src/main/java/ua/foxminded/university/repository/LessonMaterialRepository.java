package ua.foxminded.university.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.university.info.LessonMaterial;

import java.util.List;
import java.util.Optional;

public interface LessonMaterialRepository
        extends JpaRepository<LessonMaterial, Integer> {

    List<LessonMaterial> findAllByLessonIdOrderByUploadedAtDesc(
            Integer lessonId
    );

    Optional<LessonMaterial> findByObjectKey(String objectKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select material
            from LessonMaterial material
            where material.id = :materialId
            """)
    Optional<LessonMaterial> findByIdForUpdate(
            @Param("materialId") Integer materialId
    );
}
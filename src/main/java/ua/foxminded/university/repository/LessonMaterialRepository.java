package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.LessonMaterial;
import java.util.Optional;
import java.util.List;

public interface LessonMaterialRepository
        extends JpaRepository<LessonMaterial, Integer> {

    List<LessonMaterial> findAllByLessonIdOrderByUploadedAtDesc(
            Integer lessonId
    );

    Optional<LessonMaterial> findByObjectKey(String objectKey);
}
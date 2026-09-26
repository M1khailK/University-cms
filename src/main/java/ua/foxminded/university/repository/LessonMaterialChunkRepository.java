package ua.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.foxminded.university.info.LessonMaterialChunk;

import java.util.List;

public interface LessonMaterialChunkRepository
        extends JpaRepository<LessonMaterialChunk, Long> {

    List<LessonMaterialChunk>
    findAllByMaterialIdOrderByPageNumberAscChunkIndexAsc(
            Integer materialId
    );

    void deleteAllByMaterialId(Integer materialId);
}
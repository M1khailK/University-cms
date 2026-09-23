package ua.foxminded.university.services.ingestion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.foxminded.university.info.LessonMaterial;
import ua.foxminded.university.info.LessonMaterialChunk;
import ua.foxminded.university.info.LessonMaterialStatus;
import ua.foxminded.university.repository.LessonMaterialChunkRepository;
import ua.foxminded.university.repository.LessonMaterialRepository;
import ua.foxminded.university.services.ingestion.LessonMaterialProcessingStateService;
import ua.foxminded.university.services.ingestion.model.LessonMaterialObjectCreatedEvent;
import ua.foxminded.university.services.ingestion.model.LessonMaterialProcessingTarget;
import ua.foxminded.university.services.ingestion.model.LessonMaterialTextChunk;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonMaterialProcessingStateServiceImpl
        implements LessonMaterialProcessingStateService {

    private static final int MAX_FAILURE_REASON_LENGTH = 1_000;

    private final LessonMaterialRepository lessonMaterialRepository;
    private final LessonMaterialChunkRepository chunkRepository;
    private final Clock clock;

    @Override
    @Transactional
    public Optional<LessonMaterialProcessingTarget> startProcessing(
            LessonMaterialObjectCreatedEvent event
    ) {
        Objects.requireNonNull(
                event,
                "Object-created event must not be null."
        );

        LessonMaterial material = lessonMaterialRepository
                .findByObjectKey(event.objectKey())
                .orElseThrow(() -> new IllegalStateException(
                        "Lesson material not found for object key: "
                                + event.objectKey()
                ));

        if (!Objects.equals(
                material.getS3VersionId(),
                event.versionId()
        )) {
            throw new IllegalStateException(
                    "Lesson material version does not match S3 event."
            );
        }

        if (isTerminal(material.getStatus())) {
            return Optional.empty();
        }

        if (material.getStatus() != LessonMaterialStatus.UPLOADED
                && material.getStatus()
                != LessonMaterialStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Lesson material cannot start processing from status: "
                            + material.getStatus()
            );
        }

        material.setStatus(LessonMaterialStatus.PROCESSING);
        material.setProcessedAt(null);
        material.setFailureReason(null);

        return Optional.of(new LessonMaterialProcessingTarget(
                material.getId(),
                material.getObjectKey(),
                material.getS3VersionId()
        ));
    }

    @Override
    @Transactional
    public void completeProcessing(
            int materialId,
            List<LessonMaterialTextChunk> chunks
    ) {
        validateChunks(materialId, chunks);

        LessonMaterial material = findByIdForUpdate(materialId);

        if (isTerminal(material.getStatus())) {
            return;
        }

        requireProcessingStatus(material);

        chunkRepository.deleteAllByMaterialId(materialId);
        chunkRepository.flush();

        List<LessonMaterialChunk> entities = chunks.stream()
                .map(chunk -> toEntity(material, chunk))
                .toList();

        chunkRepository.saveAll(entities);

        material.setStatus(LessonMaterialStatus.READY);
        material.setProcessedAt(Instant.now(clock));
        material.setFailureReason(null);
    }

    @Override
    @Transactional
    public void failProcessing(
            int materialId,
            String failureReason
    ) {
        String normalizedReason = normalizeFailureReason(failureReason);

        LessonMaterial material = findByIdForUpdate(materialId);

        if (isTerminal(material.getStatus())) {
            return;
        }

        requireProcessingStatus(material);

        chunkRepository.deleteAllByMaterialId(materialId);
        chunkRepository.flush();

        material.setStatus(LessonMaterialStatus.FAILED);
        material.setProcessedAt(null);
        material.setFailureReason(normalizedReason);
    }

    private LessonMaterial findByIdForUpdate(int materialId) {
        if (materialId < 1) {
            throw new IllegalArgumentException(
                    "Material ID must be positive."
            );
        }

        return lessonMaterialRepository
                .findByIdForUpdate(materialId)
                .orElseThrow(() -> new IllegalStateException(
                        "Lesson material not found: " + materialId
                ));
    }

    private void validateChunks(
            int materialId,
            List<LessonMaterialTextChunk> chunks
    ) {
        if (materialId < 1) {
            throw new IllegalArgumentException(
                    "Material ID must be positive."
            );
        }

        Objects.requireNonNull(
                chunks,
                "Lesson material chunks must not be null."
        );

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lesson material chunks must not be empty."
            );
        }

        for (LessonMaterialTextChunk chunk : chunks) {
            Objects.requireNonNull(
                    chunk,
                    "Lesson material chunk must not be null."
            );

            if (chunk.materialId() != materialId) {
                throw new IllegalArgumentException(
                        "Chunk belongs to another lesson material."
                );
            }
        }
    }

    private void requireProcessingStatus(LessonMaterial material) {
        if (material.getStatus()
                != LessonMaterialStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Lesson material is not being processed: "
                            + material.getStatus()
            );
        }
    }

    private boolean isTerminal(LessonMaterialStatus status) {
        return status == LessonMaterialStatus.READY
                || status == LessonMaterialStatus.FAILED;
    }

    private LessonMaterialChunk toEntity(
            LessonMaterial material,
            LessonMaterialTextChunk source
    ) {
        LessonMaterialChunk entity = new LessonMaterialChunk();
        entity.setMaterial(material);
        entity.setPageNumber(source.pageNumber());
        entity.setChunkIndex(source.chunkIndex());
        entity.setText(source.text());
        return entity;
    }

    private String normalizeFailureReason(String failureReason) {
        if (failureReason == null || failureReason.isBlank()) {
            throw new IllegalArgumentException(
                    "Failure reason must not be blank."
            );
        }

        String normalized = failureReason.strip();

        if (normalized.length() <= MAX_FAILURE_REASON_LENGTH) {
            return normalized;
        }

        return normalized.substring(0, MAX_FAILURE_REASON_LENGTH);
    }
}
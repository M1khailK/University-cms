package ua.foxminded.university.info;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "lesson_materials")
@Getter
@Setter
@NoArgsConstructor
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "object_key", length = 512, nullable = false, unique = true)
    private String objectKey;

    @Column(name = "original_filename", length = 255, nullable = false)
    private String originalFilename;

    @Column(name = "content_type", length = 100, nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private LessonMaterialStatus status;

    @Column(name = "expected_size_bytes", nullable = false)
    private long expectedSizeBytes;

    @Column(name = "actual_size_bytes")
    private Long actualSizeBytes;

    @Column(name = "checksum_sha256", length = 128)
    private String checksumSha256;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "s3_version_id", length = 1024)
    private String s3VersionId;

    @Column(name = "s3_sequencer", length = 128)
    private String s3Sequencer;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
}